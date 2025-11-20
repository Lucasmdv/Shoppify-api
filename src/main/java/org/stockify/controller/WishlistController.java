/*package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stockify.dto.request.user.WishlistProductRequest;
import org.stockify.dto.request.user.WishlistProductsRequest;
import org.stockify.dto.response.WishlistResponse;
import org.stockify.dto.response.WishlistToggleResponse;
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
        return ResponseEntity.ok(wishlistService.findByUserId(userId));
    }



    @Operation(summary = "Get favorite state")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "True/False")
    )
    @GetMapping("/products/{productId}")
    public ResponseEntity<Boolean> isFavorite(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok(wishlistService.isProductInWishlist(userId,productId));
    }



    @Operation(summary = "Add a product to the wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product added and wishlist returned"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Wishlist or product not found"),
            @ApiResponse(responseCode = "409", description = "Product already exists in wishlist")
    })
    @PostMapping("/products")
    public ResponseEntity<WishlistResponse> addProduct(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @Valid @RequestBody WishlistProductRequest request) {
        return ResponseEntity.ok(wishlistService.addProduct(userId, request.getProductId()));
    }



    @Operation(summary = "Remove a product from the wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product removed and wishlist returned"),
            @ApiResponse(responseCode = "404", description = "Wishlist or product not found")
    })
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<WishlistResponse> removeProduct(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Identifier of the product to remove", example = "45")
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.removeProduct(userId, productId));
    }

    @Operation(summary = "Clear the wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist cleared"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found")
    })
    @DeleteMapping("/products")
    public ResponseEntity<WishlistResponse> clearWishlist(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(wishlistService.clearWishlist(userId));
    }

    @Operation(summary = "Toggle a product in the wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product toggled and wishlist returned"),
            @ApiResponse(responseCode = "404", description = "Wishlist or product not found")
    })
    @PatchMapping("/products/{productId}/toggle")
    public ResponseEntity<WishlistToggleResponse> toggleProduct(
            @Parameter(description = "Identifier of the user who owns the wishlist", example = "12")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Identifier of the product to toggle", example = "45")
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.toggleProduct(userId, productId));
    }
}*/
