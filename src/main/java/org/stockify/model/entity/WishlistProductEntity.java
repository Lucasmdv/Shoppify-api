package org.stockify.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlist_products",uniqueConstraints ={
@UniqueConstraint(name = "uc_wishlist_product", columnNames = {"product_id", "wishlist_id"})
})
public class WishlistProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private WishlistEntity wishlist;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistProductEntity that = (WishlistProductEntity) o;

        return product != null && that.product != null &&
                product.getId().equals(that.product.getId()) &&
                wishlist != null && that.wishlist != null &&
                wishlist.getId().equals(that.wishlist.getId());
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                product != null ? product.getId() : 0,
                wishlist != null ? wishlist.getId() : 0
        );
    }

}