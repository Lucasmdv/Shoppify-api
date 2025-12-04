package org.stockify.model.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.model.enums.NotificationType;
import org.stockify.model.event.ProductStockUpdatedEvent;
import org.stockify.model.event.ShipmentStateUpdatedEvent;
import org.stockify.model.repository.ShipmentRepository;
import org.stockify.model.service.NotificationService;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventListener {
    private final ShipmentRepository shipmentRepository;
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true)
    public void handleShipmentStateUpdate(ShipmentStateUpdatedEvent event) {
        String oldState = event.oldState() == null ? "" : event.oldState();
        String newState = event.newState() == null ? "" : event.newState();

        notifySubscribers(event.shipmentId(),
            "Cambio el estado de tu envío!",
            "Tu envio paso a estar en " + oldState + "a " + newState,
            "check-circle");
    }

    private void notifySubscribers(Long shipmentId, String title, String message, String icon) {
        Long userId = shipmentRepository.findUserIdByShipmentId(shipmentId)
                .orElseThrow(() -> new NoSuchElementException("No existe un usuario con ese id"));

        log.info("ShipmentListener: Notificando a {} usuarios. Evento: {}", userId, title);

        NotificationRequest request = NotificationRequest.builder()
                .type(NotificationType.PERSONAL)
                .title(title)
                .message(message)
                .icon(icon)
                .targetUserId(userId)
                .build();
            notificationService.createNotification(request);
    }
}