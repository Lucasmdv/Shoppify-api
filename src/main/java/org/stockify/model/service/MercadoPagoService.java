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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.MercadoPagoPreferenceResponse;
import org.stockify.dto.response.SaleResponse;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.model.entity.DetailTransactionEntity;
import org.stockify.model.entity.PaymentDetailEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.PaymentMethod;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.event.PaymentStatusUpdatedEvent;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.SaleRepository;
import org.stockify.model.repository.TransactionRepository;
import org.stockify.util.PriceCalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;

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
    private final SaleRepository saleRepository;
    private final TransactionRepository transactionRepository;
    private final SaleService saleService;
    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;
    private final PriceCalculator priceCalculator;

    public MercadoPagoPreferenceResponse createPreference(SaleRequest request) {
        if (request == null || request.getTransaction() == null ||
                request.getTransaction().getDetailTransactions() == null ||
                request.getTransaction().getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transaction with products is required to create a Mercado Pago preference");
        }

        String idempotencyKey = generateIdempotencyKey(request);
        Long transactionId;
        TransactionEntity transactionToUse = null;
        try {
            SaleResponse saleResponse = saleService.createSale(request, idempotencyKey);
            transactionId = saleResponse.getTransaction().getId();
        } catch (DataIntegrityViolationException e) {
            // Ya existe una transaccion PENDING con la misma key: reutilizarla
            transactionToUse = transactionRepository
                    .findFirstByIdempotencyKeyAndPaymentStatusAndType(
                            idempotencyKey,
                            PaymentStatus.PENDING,
                            TransactionType.SALE)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "Existing pending transaction not found for idempotency key"));
            transactionId = transactionToUse.getId();
            log.info("Reusing pending transaction {} with idempotencyKey {}", transactionId, idempotencyKey);
        }

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

        List<PreferenceItemRequest> items = transactionToUse != null
                ? buildItemsFromTransaction(transactionToUse)
                : request.getTransaction().getDetailTransactions().stream()
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

        if (userEmail != null && !userEmail.isBlank()) {
            preferenceRequestBuilder.payer(PreferencePayerRequest.builder().email(userEmail).build());
        }

        String notificationUrl = System.getenv("NOTIFICATION_URL");
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            preferenceRequestBuilder.notificationUrl(notificationUrl);
        }

        PreferenceRequest preferenceRequest = preferenceRequestBuilder.build();

        try {
            Preference preference = new PreferenceClient().create(preferenceRequest);
            return new MercadoPagoPreferenceResponse(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint(),
                    transactionId
            );
        } catch (MPApiException e) {
            String apiMessage = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error creating Mercado Pago preference: " + apiMessage, e);
        } catch (MPException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error creating Mercado Pago preference: " + e.getMessage(), e);
        }
    }

    private String generateIdempotencyKey(SaleRequest request) {
        String userPart = request.getUserId() != null ? String.valueOf(request.getUserId()) : "anon";

        String itemsPart = request.getTransaction().getDetailTransactions().stream()
                .sorted(Comparator.comparing(DetailTransactionRequest::getProductID))
                .map(d -> d.getProductID() + ":" + (d.getQuantity() == null ? 1 : d.getQuantity()))
                .collect(java.util.stream.Collectors.joining("|"));

        String canonical = userPart + "|" + itemsPart;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private List<PreferenceItemRequest> buildItemsFromTransaction(TransactionEntity transaction) {
        if (transaction.getDetailTransactions() == null || transaction.getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Existing transaction has no details to build a preference");
        }

        return transaction.getDetailTransactions().stream()
                .map(this::buildItem)
                .toList();
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

    private PreferenceItemRequest buildItem(DetailTransactionEntity detail) {
        ProductEntity product = detail.getProduct();
        if (product == null || product.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product information missing in existing transaction detail");
        }
        DetailTransactionRequest request = new DetailTransactionRequest(product.getId(), detail.getQuantity());
        return buildItem(request);
    }

    public ResponseEntity<String> handleWebhook(Map<String, String> params,
            Map<String, Object> body,
            String xSignature,
            String xRequestId) {

        String dataId = params.get("data.id");
        String action = params.get("action");
        String topic = params.get("type");

        if (body != null) {
            if (dataId == null) {
                dataId = extractDataId(body);
            }
            if (action == null) {
                action = (String) body.get("action");
            }
            if (topic == null) {
                topic = (String) body.get("type");
            }
        }

        if (topic == null && action != null && action.startsWith("payment")) {
            topic = "payment";
        }

        try {
            String paymentId = dataId;

            log.info("Notificacion recibida - Tipo: {}, ID de pago: {}", topic, paymentId);
            if (paymentId != null) {
                try {
                    processWebhookNotification(topic, paymentId);
                    return ResponseEntity.ok("Notificacion recibida correctamente");
                } catch (RuntimeException e) {
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("No se pudo encontrar el pago")
                            || msg.contains("Payment not found"))) {
                        log.warn("Pago no encontrado o invalido, deteniendo reintentos: {}", msg);
                        return ResponseEntity.ok("Notificacion procesada (Pago no encontrado)");
                    }
                    log.warn("Error de negocio al procesar webhook: {}", msg);
                    return ResponseEntity.ok("Notificacion recibida (Error de negocio)");
                }
            }

            log.warn("ID de pago no proporcionado en webhook. Se responde 200 para cortar reintentos.");
            return ResponseEntity.ok("Notificacion recibida sin ID de pago");
        } catch (Exception e) {
            log.error("Error al procesar notificacion de webhook", e);
            return ResponseEntity.ok("Notificacion recibida (error interno)");
        }
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

        PaymentStatus oldStatus = transaction.getPaymentStatus();

        PaymentStatus newStatus = mapStatus(payment.getStatus());
        transaction.setPaymentStatus(newStatus);
        // Actualiza detalles del pago (no se almacena informacion sensible completa)
        applyPaymentDetails(transaction, payment);
        PaymentMethod mappedMethod = mapPaymentMethod(payment.getPaymentTypeId());
        if (mappedMethod != null) {
            transaction.setPaymentMethod(mappedMethod);
        }
        transactionRepository.save(transaction);

            log.info("Updated transaction {} status to {}", transactionId, newStatus);

            if (oldStatus != newStatus) {
                Long saleId = saleRepository.findSaleIdByTransactionId(transactionId).orElse(null);
                Long userId = saleRepository.findUserIdByTransactionId(transactionId).orElse(null);
                eventPublisher.publishEvent(new PaymentStatusUpdatedEvent(
                        saleId,
                        oldStatus,
                        newStatus,
                        userId
                ));
            }

            // Logic to restore stock using TransactionService if payment failed
            if (isFailureStatus(newStatus)) {
                log.info("Payment failed for transaction {}. Restoring stock...", transactionId);
                transactionService.restoreStock(transaction);
            }

        } catch (MPApiException e) {
            int status = e.getStatusCode();
            String content = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            if (status == 404 || (content != null && content.contains("Payment not found"))) {
                log.warn("Payment not found for ID {}. Ignoring webhook to avoid retries. Response: {}", id, content);
                return;
            }
            log.error("MercadoPago API Error: {}", content);
            throw new RuntimeException("Error fetching payment from MercadoPago: " + content, e);
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

    private void applyPaymentDetails(TransactionEntity transaction, Payment payment) {
        PaymentDetailEntity detail = transaction.getPaymentDetail();
        if (detail == null) {
            detail = new PaymentDetailEntity();
            detail.setTransaction(transaction);
            transaction.setPaymentDetail(detail);
        }

        detail.setPaymentId(payment.getId() != null ? String.valueOf(payment.getId()) : null);
        detail.setStatus(payment.getStatus());
        detail.setStatusDetail(payment.getStatusDetail());
        detail.setPaymentMethodId(payment.getPaymentMethodId());
        detail.setPaymentTypeId(payment.getPaymentTypeId());
        detail.setIssuerId(payment.getIssuerId() != null ? String.valueOf(payment.getIssuerId()) : null);
        detail.setInstallments(payment.getInstallments());

        if (payment.getCard() != null) {
            detail.setCardLastFour(payment.getCard().getLastFourDigits());
            if (payment.getCard().getCardholder() != null) {
                detail.setCardholderName(payment.getCard().getCardholder().getName());
            }
        }

        detail.setStatementDescriptor(payment.getStatementDescriptor());
        detail.setTransactionAmount(payment.getTransactionAmount());

        if (payment.getTransactionDetails() != null) {
            detail.setNetReceivedAmount(payment.getTransactionDetails().getNetReceivedAmount());
        }

        if (payment.getPayer() != null) {
            detail.setPayerEmail(payment.getPayer().getEmail());
            detail.setPayerId(payment.getPayer().getId());
        }

        detail.setDateApproved(toLocalDateTime(payment.getDateApproved()));
        detail.setDateCreated(toLocalDateTime(payment.getDateCreated()));
    }

    private PaymentMethod mapPaymentMethod(String paymentTypeId) {
        if (paymentTypeId == null) return null;
        return switch (paymentTypeId) {
            case "credit_card" -> PaymentMethod.CREDIT;
            case "debit_card" -> PaymentMethod.DEBIT;
            case "account_money", "wallet" -> PaymentMethod.DIGITAL;
            default -> null;
        };
    }

    private LocalDateTime toLocalDateTime(java.util.Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private LocalDateTime toLocalDateTime(java.time.OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDateTime();
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

    private String extractDataId(Map<String, Object> body) {
        Object dataObj = body.get("data");
        if (dataObj instanceof Map<?, ?> dataMap) {
            Object idObj = dataMap.get("id");
            if (idObj instanceof String) {
                return (String) idObj;
            } else if (idObj != null) {
                return String.valueOf(idObj);
            }
        }
        return null;
    }
}
