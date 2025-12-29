package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.stockify.dto.response.WishlistResponse;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.entity.WishlistEntity;
import org.stockify.model.entity.WishlistProductEntity;
import org.stockify.model.exception.DuplicatedUniqueConstraintException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.WishlistMapper;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.UserRepository;
import org.stockify.model.repository.WishlistRepository;

@Service
@AllArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistMapper wishlistMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistResponse getWishlist(Long userId) {
        return wishlistMapper.toResponse(resolveWishlist(userId));
    }

    public ResponseEntity<HttpStatus> createWishlist(Long userId){
    UserEntity user = resolveUser(userId);
        if(wishlistRepository.findByUserId(userId).isPresent()){
            throw new DuplicatedUniqueConstraintException("Wishlist for user id " + userId + " already exists");
        }

        WishlistEntity wishlist = WishlistEntity.builder().user(user).name("Favorites").build();
        user.setWishlist(wishlist);

    return ResponseEntity.ok(HttpStatus.CREATED);
    }

    public boolean toggleProduct(Long userId, Long productId) {
        WishlistEntity wishlist = resolveWishlist(userId);
        ProductEntity product = resolveProduct(productId);
        WishlistProductEntity wishlistProductEntity = WishlistProductEntity.builder()
                .product(product)
                .wishlist(wishlist)
                .build();

        boolean added = wishlist.addProduct(wishlistProductEntity);
        if (!added) {
            wishlist.removeProduct(wishlistProductEntity);
        }
        wishlistRepository.save(wishlist);
        return added;
    }

    public boolean isFavorite(Long userId,Long productId){
        WishlistEntity wishlist = resolveWishlist(userId);
        ProductEntity product = resolveProduct(productId);

        WishlistProductEntity wishlistProductEntity = WishlistProductEntity.builder()
                .product(product)
                .wishlist(wishlist)
                .build();

        return wishlist.getWishlistProducts().contains(wishlistProductEntity);
    }


    //Helpers
    private WishlistEntity resolveWishlist(Long userId){
        return wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wishlist with Userid " + userId + " not found"));
    }

    private UserEntity resolveUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }


    private ProductEntity resolveProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }



}
