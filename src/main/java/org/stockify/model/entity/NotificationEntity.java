package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.stockify.model.enums.NotificationStatus;
import org.stockify.model.enums.NotificationType;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class NotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private Instant createdAt;

    private Instant publishAt;

    private Instant expiresAt;

    @Column(length = 120, nullable = false)
    private String title;
    @Column(length = 500, nullable = false)
    private String message;
    @Column(length = 120)
    private String icon;
    private Long targetUserId;
    private Long relatedProductId;
    private Long relatedSaleId;
}
