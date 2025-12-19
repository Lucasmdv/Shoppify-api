package org.stockify.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "stores")

public class StoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 15)
    @NotNull
    @Column(name = "store_name", nullable = false, length = 15)
    private String storeName;

    @Size(max = 100)
    @NotNull
    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Size(max = 20)
    @NotNull
    @Column(name = "postal_code", length = 20, nullable = false)
    private String postalCode;

    @Size(max = 100)
    @Column(name = "facebook", length = 100)
    private String facebook;

    @Size(max = 100)
    @Column(name = "instagram", length = 100)
    private String instagram;

    @Size(max = 100)
    @Column(name = "twitter", length = 100)
    private String twitter;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<TransactionEntity> transactions;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarouselItem> homeCarousel = new ArrayList<>();

    @Column(name = "shipping_cost_small", precision = 10, scale = 2)
    private java.math.BigDecimal shippingCostSmall;

    @Column(name = "shipping_cost_medium", precision = 10, scale = 2)
    private java.math.BigDecimal shippingCostMedium;

    @Column(name = "shipping_cost_large", precision = 10, scale = 2)
    private java.math.BigDecimal shippingCostLarge;
}
