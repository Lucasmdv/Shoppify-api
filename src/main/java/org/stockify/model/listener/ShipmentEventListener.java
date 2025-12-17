package org.stockify.model.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.model.enums.NotificationType;
import org.stockify.model.event.ShipmentStateUpdatedEvent;
import org.stockify.model.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleShipmentStateUpdate(ShipmentStateUpdatedEvent event) {
        String oldState = event.oldState().getTranslation();
        String newState = event.newState().getTranslation();

        notifySubscribers(event.shipmentId(),
                "Actualizacion de tu envio",
                "Tu envio paso de " + oldState + " a " + newState,
                "truck",
                event.saleId(),
                event.userId());
    }

    private void notifySubscribers(Long shipmentId, String title, String message, String icon, Long saleId,
            Long userId) {
        if (userId == null) {
            log.info("ShipmentListener: No user linked to shipment {}. Skipping notification.", shipmentId);
            return;
        }

        log.info("ShipmentListener: Notificando a usuario {}. Evento: {}", userId, title);

        NotificationRequest request = NotificationRequest.builder()
                .type(NotificationType.PERSONAL)
                .title(saleId != null ? title + " #" + saleId : title)
                .message(message)
                .icon(icon)
                .targetUserId(userId)
                .relatedSaleId(saleId)
                .build();

        try {
            notificationService.createNotification(request);
            log.info("ShipmentListener: Notificación persistida exitosamente.");
        } catch (Exception e) {
            log.error("ShipmentListener: Error al persistir notificación: {}", e.getMessage());
        }
    }
}
