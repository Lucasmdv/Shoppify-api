package org.stockify.model.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.repository.ProductRepository;
import org.stockify.util.PriceCalculator;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private static final String FRONTEND_BASE = System.getenv().getOrDefault("FRONTEND_URL", "localhost:4200");
    private static final String DEFAULT_CURRENCY = "ARS";
    private static final String SUCCESS_URL = FRONTEND_BASE + "/auth/checkout/success";
    private static final String PENDING_URL = FRONTEND_BASE + "/auth/checkout/pending";
    //DEFAULT
    // private static final String FAILURE_URL = FRONTEND_BASE + "/auth/checkout/failure";
    private static final String FAILURE_URL = FRONTEND_BASE + "/cart";

    private final ProductRepository productRepository;
    private final PriceCalculator priceCalculator;
    private final SaleService saleService;
    private final PaymentClient paymentClient;

    public MercadoPagoService(ProductRepository productRepository, PriceCalculator priceCalculator, SaleService saleService) {
        this.productRepository = productRepository;
        this.priceCalculator = priceCalculator;
        this.saleService = saleService;
        this.paymentClient = new PaymentClient();
    }

    public Preference createPreference(SaleRequest request) {
        if (request == null || request.getTransaction() == null ||
                request.getTransaction().getDetailTransactions() == null ||
                request.getTransaction().getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction with products is required to create a Mercado Pago preference");
        }

        List<PreferenceItemRequest> items = request.getTransaction().getDetailTransactions().stream()
                .map(this::buildItem)
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(SUCCESS_URL)
                .pending(PENDING_URL)
                .failure(FAILURE_URL)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        try {
            saleService.createSale(request);
            return new PreferenceClient().create(preferenceRequest);
        } catch (MPApiException e) {
            String apiMessage = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error creating Mercado Pago preference: " + apiMessage, e);
        } catch (MPException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error creating Mercado Pago preference: " + e.getMessage(), e);
        }
    }

    /**
     * Procesa una notificación de webhook de MercadoPago
     * @param topic Tipo de notificación (ej: "payment")
     * @param paymentId ID del pago en MercadoPago
     */
    @Transactional
    public void processWebhookNotification(String topic, String paymentId) {
        try {
            log.info("Procesando notificación de webhook - Tema: {}, ID de pago: {}", topic, paymentId);
            
            // Obtener los detalles del pago de MercadoPago
            Payment payment = paymentClient.get(Long.parseLong(paymentId));
            
            if (payment == null) {
                log.error("No se pudo encontrar el pago con ID: {}", paymentId);
                return;
            }
            
            // Obtener el estado del pago
            String status = payment.getStatus();
            String statusDetail = payment.getStatusDetail();
            String externalReference = payment.getExternalReference();
            
            log.info("Estado del pago {}: {} - {}", paymentId, status, statusDetail);
            
            // Mapear el estado de MercadoPago a PaymentStatus
            PaymentStatus paymentStatus = mapMercadoPagoStatusToPaymentStatus(status, statusDetail);
            
            if (externalReference != null && !externalReference.isEmpty()) {
                try {
                    // Actualizar el estado del pago en la transacción correspondiente
                    Long transactionId = Long.parseLong(externalReference);
                    transactionService.updatePaymentStatus(transactionId, paymentStatus);
                    log.info("Estado de pago actualizado para la transacción {}: {}", transactionId, paymentStatus);
                } catch (NumberFormatException e) {
                    log.error("El externalReference '{}' no es un ID de transacción válido", externalReference);
                } catch (Exception e) {
                    log.error("Error al actualizar el estado del pago para la transacción {}", externalReference, e);
                }
            } else {
                log.warn("El pago {} no tiene externalReference, no se puede actualizar el estado de la transacción", paymentId);
            }
            
            // Registrar la notificación recibida
            log.info("Notificación procesada correctamente para el pago: {}", paymentId);
            
        } catch (MPApiException e) {
            String errorMessage = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            log.error("Error en la API de MercadoPago al procesar notificación: {}", errorMessage, e);
            throw new RuntimeException("Error al procesar notificación de pago: " + errorMessage, e);
        } catch (Exception e) {
            log.error("Error al procesar notificación de webhook", e);
            throw new RuntimeException("Error al procesar notificación de pago: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mapea el estado de MercadoPago a PaymentStatus
     */
    private PaymentStatus mapMercadoPagoStatusToPaymentStatus(String status, String statusDetail) {
        if (status == null) {
            return PaymentStatus.PENDING;
        }
        
        return switch (status.toLowerCase()) {
            case "approved" -> PaymentStatus.APPROVED;
            case "rejected" -> "cc_rejected_insufficient_amount".equals(statusDetail) ? 
                    PaymentStatus.REJECTED : PaymentStatus.PENDING;
            case "cancelled" -> PaymentStatus.CANCELLED;
            case "refunded", "charged_back" -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.PENDING; // Incluye "pending", "in_process", etc.
        };
    }
    }
    
    private PreferenceItemRequest buildItem(DetailTransactionRequest detail) {
        ProductEntity product = productRepository.findById(detail.getProductID())
                .orElseThrow(() -> new NotFoundException("Product with id " + detail.getProductID() + " not found"));

        BigDecimal basePrice = product.getPrice();
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with id " + product.getId() + " has no valid price");
        }

        BigDecimal unitPrice = priceCalculator.calculateDiscountPrice(product);
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            unitPrice = basePrice;
        }

        int quantity = detail.getQuantity() == null || detail.getQuantity() <= 0
                ? 1
                : Math.toIntExact(detail.getQuantity());

        return PreferenceItemRequest.builder()
                .id(String.valueOf(product.getId()))
                .title(product.getName())
                .description(product.getDescription())
                .pictureUrl(product.getImgURL())
                .quantity(quantity)
                .currencyId(DEFAULT_CURRENCY)
                .unitPrice(unitPrice)
                .build();
    }
}
