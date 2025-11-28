package org.stockify.model.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.model.enums.NotificationType;
import org.stockify.model.event.ProductDiscountUpdatedEvent;
import org.stockify.model.event.ProductStockUpdatedEvent;
import org.stockify.model.repository.WishlistRepository;
import org.stockify.model.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class WishlistEventListener {

    private final WishlistRepository wishlistRepository;
    private final NotificationService notificationService;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true)
    public void handleStockUpdate(ProductStockUpdatedEvent event) {
        long oldStock = event.oldStock() == null ? 0L : event.oldStock();
        long newStock = event.newStock() == null ? 0L : event.newStock();

        boolean justRestocked = oldStock == 0 && newStock > 0;
        boolean justCrossedLowStockLine = oldStock > LOW_STOCK_THRESHOLD &&
                newStock <= LOW_STOCK_THRESHOLD &&
                newStock > 0;

        if (justRestocked) {
            notifySubscribers(event.productId(),
                    "Volvio el stock!",
                    "El producto " + event.productName() + " ya está disponible nuevamente.",
                    "check-circle");
        } else if (justCrossedLowStockLine) {
            notifySubscribers(event.productId(),
                    "Quedan pocos!",
                    "Apurate! Solo quedan " + newStock + " unidades de " + event.productName(),
                    "hourglass");
        }
    }

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true)
    public void handleProductDiscountUpdate(ProductDiscountUpdatedEvent event) {
        if (event.discount() != null && event.discount().doubleValue() > 0) {
            notifySubscribers(event.productId(),
                    "Oportunidad!",
                    event.productName() + " bajó de precio! Aprovecha un " + event.discount() + "% OFF.",
                    "tag");
        }
    }

    private void notifySubscribers(Long productId, String title, String message, String icon) {
        List<Long> userIds = wishlistRepository.findUserIdsByProductId(productId);
        if (userIds.isEmpty()) return;

        log.info("WishlistListener: Notificando a {} usuarios. Evento: {}", userIds.size(), title);

        userIds.forEach(userId -> {
            NotificationRequest request = NotificationRequest.builder()
                    .type(NotificationType.PRODUCT_ALERT)
                    .title(title)
                    .message(message)
                    .icon(icon)
                    .targetUserId(userId)
                    .relatedProductId(productId)
                    .build();

            notificationService.createNotification(request);
        });
    }
}
