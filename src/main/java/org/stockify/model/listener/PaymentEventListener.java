package org.stockify.model.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.model.enums.NotificationType;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.event.PaymentStatusUpdatedEvent;
import org.stockify.model.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handlePaymentStatusUpdate(PaymentStatusUpdatedEvent event) {
        Long userId = event.userId();
        if (userId == null) {
            log.info("PaymentEventListener: No user linked to sale {}. Skipping notification.", event.saleId());
            return;
        }

        NotificationRequest request = NotificationRequest.builder()
                .type(NotificationType.PERSONAL)
                .title(buildTitle(event.saleId()))
                .message(buildMessage(event.newStatus()))
                .icon(resolveIcon(event.newStatus()))
                .targetUserId(userId)
                .relatedSaleId(event.saleId())
                .build();

        notificationService.createNotification(request);
    }

    private String buildTitle(Long saleId) {
        return saleId != null ? "Actualizacion de tu pedido #" + saleId : "Actualización de pedido";
    }

    private String buildMessage(PaymentStatus newStatus) {
        return switch (newStatus) {
            case APPROVED -> "¡Tu pago fue acreditado con éxito!";
            case CANCELLED -> "Tu pago ha sido cancelado.";
            case REJECTED -> "Lamentamos informarte que tu pago fue rechazado.";
            case REFUNDED -> "Tu pago ha sido reembolsado.";
            case PENDING -> "Tu pago se encuentra en estado pendiente de aprobación.";
        };
    }

    private String translate(PaymentStatus status) {
        return switch (status) {
            case APPROVED -> "aprobado";
            case CANCELLED -> "cancelado";
            case REJECTED -> "rechazado";
            case REFUNDED -> "reembolsado";
            case PENDING -> "pendiente";
        };
    }

    private String resolveIcon(PaymentStatus status) {
        return switch (status) {
            case APPROVED -> "check-circle";
            case REFUNDED -> "rotate-ccw";
            case REJECTED, CANCELLED -> "x-circle";
            default -> "credit-card";
        };
    }
}