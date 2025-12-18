package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.stockify.model.enums.OrderStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class ShipmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private SaleEntity sale;

    @Column(name = "pickup")
    private Boolean pickup;

    @Column(name = "adress")
    private String adress;
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "shipment_cost", precision = 10, scale = 2)
    private java.math.BigDecimal shipmentCost;
}