package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stockify.dto.response.WishlistResponse;
import org.stockify.model.service.WishlistService;

@RestController
@RequestMapping("/user/{userId}/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Endpoints for managing user wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "Get wishlist for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist returned successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist or user not found")
    })
    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @Operation(summary = "Toggle a product in the wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist or product not found")
    })
    @PatchMapping("/products/{productId}/toggle")
    public ResponseEntity<Boolean> toggleProduct(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Identifier of the product to toggle", example = "45")
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.toggleProduct(userId, productId));
    }

    @Operation(summary = "Check if product is in wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns true if product is in wishlist"),
            @ApiResponse(responseCode = "404", description = "Wishlist or product not found")
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<Boolean> isFavorite(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Identifier of the product to check", example = "45")
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.isFavorite(userId, productId));
    }
}
