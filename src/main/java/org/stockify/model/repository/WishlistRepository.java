package org.stockify.model.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stockify.model.entity.WishlistEntity;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long>{
    Optional<WishlistEntity> findByUserId(Long userId);

    @Query("""
        SELECT w.user.id
        FROM WishlistEntity w
        JOIN w.wishlistProducts wp
        WHERE wp.product.id = :productId
    """)
    List<Long> findUserIdsByProductId(@Param("productId") Long productId);
}
