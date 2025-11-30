package org.stockify.model.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.NotificationRead;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {

    boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);

    Optional<NotificationRead> findByUserIdAndNotificationId(Long userId, Long notificationId);

    Page<NotificationRead> findByUserId(Long userId, Pageable pageable);
}
