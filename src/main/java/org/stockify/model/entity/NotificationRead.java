package org.stockify.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "notificationId"})
})
public class NotificationRead {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long notificationId;

    @CreationTimestamp
    @Column(name = "read_at", updatable = false)
    private LocalDateTime readAt;

}
