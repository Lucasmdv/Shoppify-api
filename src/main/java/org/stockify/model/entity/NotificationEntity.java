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
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private Instant createdAt;

    private Instant publishAt;

    private Instant expiresAt;

    private String title;
    private String message;
    private String icon;
    private Long targetUserId;
    private Long relatedProductId;
}
