package org.stockify.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stockify.model.entity.WishlistEntity;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long>{
    Optional<WishlistEntity> findByUserId(Long userId);
}
