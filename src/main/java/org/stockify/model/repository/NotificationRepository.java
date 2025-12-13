package org.stockify.model.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stockify.model.entity.NotificationEntity;
import org.stockify.model.enums.NotificationStatus;
import org.stockify.model.projections.NotificationSummary;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
        SELECT
            n.id AS id,
            n.title AS title,
            n.message AS message,
            n.type AS type,
            n.icon AS icon,
            n.publishAt AS publishAt,
            n.createdAt AS createdAt,
            n.relatedProductId AS relatedProductId,
            CASE WHEN nr.id IS NOT NULL THEN true ELSE false END AS read,
            CASE WHEN nh.id IS NOT NULL THEN true ELSE false END AS hidden
        FROM NotificationEntity n
        LEFT JOIN NotificationRead nr ON n.id = nr.notificationId AND nr.userId = :userId
        LEFT JOIN NotificationHidden nh ON n.id = nh.notificationId AND nh.userId = :userId
        LEFT JOIN WishlistEntity w ON w.user.id = :userId
        LEFT JOIN w.wishlistProducts wp ON wp.product.id = n.relatedProductId
        WHERE
            (
                (n.type = 'PERSONAL' AND n.targetUserId = :userId)
                OR (n.type = 'GLOBAL')
                OR (n.type = 'PRODUCT_ALERT' AND wp.id IS NOT NULL)
            )
            AND n.status = 'PUBLISHED'
            AND (n.publishAt IS NULL OR n.publishAt <= CURRENT_TIMESTAMP)
            AND (n.expiresAt IS NULL OR n.expiresAt > CURRENT_TIMESTAMP)
            AND nh.id IS NULL
        ORDER BY n.createdAt DESC
    """)
    Page<NotificationSummary> findAllProjectedByUserId(@Param("userId") Long userId,
                                                       Pageable pageable);

    List<NotificationEntity> findAllByStatusAndPublishAtBefore(NotificationStatus status, Instant publishAt);

    Page<NotificationEntity> findAllByTargetUserIdAndPublishAtBeforeOrderByPublishAtDesc(Long targetUserId, Instant publishAt, Pageable pageable);
}
