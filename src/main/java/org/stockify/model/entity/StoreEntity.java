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

    @Size(max = 100)
    @NotNull
    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Size(max = 255)
    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 100)
    @NotNull
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Size(max = 255)
    @Column(name = "facebook", length = 255)
    private String facebook;

    @Size(max = 255)
    @Column(name = "instagram", length = 255)
    private String instagram;

    @Size(max = 255)
    @Column(name = "twitter", length = 255)
    private String twitter;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<TransactionEntity> transactions;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarouselItem> homeCarousel = new ArrayList<>();

}
