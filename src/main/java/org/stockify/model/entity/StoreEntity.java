package org.stockify.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;

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

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<TransactionEntity> transactions;

    @ElementCollection
    @CollectionTable(name = "store_home_carousel", joinColumns = @JoinColumn(name = "store_id"))
    private List<CarouselItem> homeCarousel = new ArrayList<>();



}
