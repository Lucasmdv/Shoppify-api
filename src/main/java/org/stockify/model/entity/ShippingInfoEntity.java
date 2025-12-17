package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipping")
public class ShippingInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "pickup")
    private Boolean pickup;

    @Column(name = "adress")
    private String adress;

    @OneToOne
    @JoinColumn(name = "sale_id", nullable = false, unique = true)
    private SaleEntity sale;
}