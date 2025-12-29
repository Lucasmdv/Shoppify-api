package org.stockify.model.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.stockify.model.entity.NotificationHidden;

public interface NotificationHiddenRepository extends JpaRepository<NotificationHidden, Long> {
    boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);
    Optional<NotificationHidden> findByUserIdAndNotificationId(Long userId, Long notificationId);
}
