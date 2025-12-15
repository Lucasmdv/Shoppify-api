package org.stockify.model.service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.SaleResponse;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.TransactionRepository;
import org.stockify.util.PriceCalculator;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {

    private static final String FRONTEND_BASE = System.getenv().getOrDefault("FRONTEND_URL", "localhost:4200");
    private static final String DEFAULT_CURRENCY = "ARS";
    private static final String SUCCESS_URL = FRONTEND_BASE + "/auth/checkout/success";
    private static final String PENDING_URL = FRONTEND_BASE + "/auth/checkout/pending";
    // DEFAULT
    // private static final String FAILURE_URL = FRONTEND_BASE +
    // "/auth/checkout/failure";
    private static final String FAILURE_URL = FRONTEND_BASE + "/cart";

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final SaleService saleService;
    private final TransactionService transactionService;
    private final PriceCalculator priceCalculator;

    public Preference createPreference(SaleRequest request) {
        if (request == null || request.getTransaction() == null ||
                request.getTransaction().getDetailTransactions() == null ||
                request.getTransaction().getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transaction with products is required to create a Mercado Pago preference");
        }

        SaleResponse saleResponse = saleService.createSale(request);
        Long transactionId = saleResponse.getTransaction().getId();

        // Intentar obtener el email del usuario desde el SecurityContext
        String userEmail = null;
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails ud) {
                    userEmail = ud.getUsername();
                } else if (auth.getName() != null && !auth.getName().isBlank()) {
                    userEmail = auth.getName();
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener email del Contexto de Seguridad: {}", e.getMessage());
        }

        List<PreferenceItemRequest> items = request.getTransaction().getDetailTransactions().stream()
                .map(this::buildItem)
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(SUCCESS_URL)
                .pending(PENDING_URL)
                .failure(FAILURE_URL)
                .build();

        PreferenceRequest.PreferenceRequestBuilder preferenceRequestBuilder = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(String.valueOf(transactionId));

        // Si obtuvimos email, añadirlo como payer en la preference
        if (userEmail != null && !userEmail.isBlank()) {
            preferenceRequestBuilder.payer(PreferencePayerRequest.builder().email(userEmail).build());
        }

        String notificationUrl = System.getenv("NOTIFICATION_URL");
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            preferenceRequestBuilder.notificationUrl(notificationUrl);
        }

        PreferenceRequest preferenceRequest = preferenceRequestBuilder.build();

        try {
            return new PreferenceClient().create(preferenceRequest);
        } catch (MPApiException e) {
            String apiMessage = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error creating Mercado Pago preference: " + apiMessage, e);
        } catch (MPException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error creating Mercado Pago preference: " + e.getMessage(), e);
        }
    }

    private PreferenceItemRequest buildItem(DetailTransactionRequest detail) {
        ProductEntity product = productRepository.findById(detail.getProductID())
                .orElseThrow(() -> new NotFoundException("Product with id " + detail.getProductID() + " not found"));

        BigDecimal basePrice = product.getPrice();
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product with id " + product.getId() + " has no valid price");
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

    public void processWebhookNotification(String topic, String id) {
        if (!"payment".equals(topic))
            return;

        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(id));

            if (payment == null) {
                log.error("Payment not found for ID: {}", id);
                return;
            }

            String externalReference = payment.getExternalReference();
            if (externalReference == null) {
                log.warn("Payment {} has no external reference. Cannot link to transaction.", id);
                return;
            }

            Long transactionId;
            try {
                transactionId = Long.parseLong(externalReference);
            } catch (NumberFormatException e) {
                log.error("Invalid external reference format: {}", externalReference);
                return;
            }

            TransactionEntity transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new NotFoundException("Transaction not found for ID: " + transactionId));

            PaymentStatus newStatus = mapStatus(payment.getStatus());
            transaction.setPaymentStatus(newStatus);
            transactionRepository.save(transaction);

            log.info("Updated transaction {} status to {}", transactionId, newStatus);

            // Logic to restore stock using TransactionService if payment failed
            if (isFailureStatus(newStatus)) {
                log.info("Payment failed for transaction {}. Restoring stock...", transactionId);
                transactionService.restoreStock(transaction);
            }

        } catch (MPApiException e) {
            log.error("MercadoPago API Error: {}", e.getApiResponse().getContent());
            throw new RuntimeException("Error fetching payment from MercadoPago", e);
        } catch (MPException e) {
            log.error("MercadoPago Error: {}", e.getMessage());
            throw new RuntimeException("Error processing webhook", e);
        }
    }

    private boolean isFailureStatus(PaymentStatus status) {
        return status == PaymentStatus.CANCELLED ||
                status == PaymentStatus.REJECTED ||
                status == PaymentStatus.REFUNDED;
    }

    private PaymentStatus mapStatus(String mpStatus) {
        if (mpStatus == null)
            return PaymentStatus.PENDING;

        return switch (mpStatus) {
            case "approved" -> PaymentStatus.APPROVED;
            case "rejected" -> PaymentStatus.REJECTED;
            case "cancelled" -> PaymentStatus.CANCELLED;
            case "refunded", "charged_back" -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.PENDING;
        };
    }
}
