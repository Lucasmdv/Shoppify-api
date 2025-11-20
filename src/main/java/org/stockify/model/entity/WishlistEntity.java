package org.stockify.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlists")
public class WishlistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "wishlist",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt desc")
    private Set<WishlistProductEntity> wishlistProducts = new LinkedHashSet<>();

    public boolean addProduct(WishlistProductEntity item) {
        boolean rta = this.wishlistProducts.add(item);
        item.setWishlist(this);
        return rta;
    }

    public boolean removeProduct(WishlistProductEntity item) {
        boolean rta = this.wishlistProducts.remove(item);
        item.setWishlist(null);
        return rta;
    }

}
