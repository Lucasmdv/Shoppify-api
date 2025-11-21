package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.stockify.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private UserEntity client;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "products_orders",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductEntity> products;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private TransactionEntity transaction;
}