package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.cart.CartItemRequest;
import org.stockify.dto.request.cart.UpdateCartItemRequest;
import org.stockify.dto.response.CartResponse;
import org.stockify.model.service.CartService;

@RestController
@RequestMapping("/user/{userId}/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Endpoints for managing user carts")
public class CartController {

        private final CartService cartService;

        @Operation(summary = "Get cart for a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cart returned successfully"),
                        @ApiResponse(responseCode = "404", description = "Cart or user not found")
        })
        @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.user.id")
        @GetMapping
        public ResponseEntity<CartResponse> getCartByUser(
                        @Parameter(description = "Identifier of the user who owns the cart", example = "12") @PathVariable("userId") Long userId) {
                return ResponseEntity.ok(cartService.findByUserId(userId));
        }

        @Operation(summary = "Add an item to a user's cart")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Item added and cart returned"),
                        @ApiResponse(responseCode = "400", description = "Invalid payload"),
                        @ApiResponse(responseCode = "404", description = "Cart or product not found"),
                        @ApiResponse(responseCode = "409", description = "Insufficient stock or duplicated cart")
        })
        @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.user.id")
        @PostMapping("/items")
        public ResponseEntity<CartResponse> addItem(
                        @Parameter(description = "Identifier of the user who owns the cart", example = "12") @PathVariable("userId") Long userId,
                        @Valid @RequestBody CartItemRequest request) {
                CartResponse cart = cartService.addItem(request, userId);
                return ResponseEntity.ok(cart);
        }

        @Operation(summary = "Overwrite the quantity of an existing cart item")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Item updated and cart returned"),
                        @ApiResponse(responseCode = "400", description = "Invalid payload"),
                        @ApiResponse(responseCode = "404", description = "Cart or item not found")
        })
        @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.user.id")
        @PutMapping("/items/{itemId}")
        public ResponseEntity<CartResponse> updateItem(
                        @Parameter(description = "Identifier of the user who owns the cart", example = "12") @PathVariable("userId") Long userId,
                        @Parameter(description = "Identifier of the cart item", example = "7") @PathVariable Long itemId,
                        @Valid @RequestBody UpdateCartItemRequest request) {
                CartResponse cart = cartService.updateItem(itemId, userId, request.getQuantity());
                return ResponseEntity.ok(cart);
        }

        @Operation(summary = "Remove a single item from the cart")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Item removed and cart returned"),
                        @ApiResponse(responseCode = "404", description = "Cart or item not found")
        })
        @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.user.id")
        @DeleteMapping("/items/{itemId}")
        public ResponseEntity<CartResponse> removeItem(
                        @Parameter(description = "Identifier of the user who owns the cart", example = "12") @PathVariable("userId") Long userId,
                        @Parameter(description = "Identifier of the cart item", example = "7") @PathVariable Long itemId) {
                CartResponse cart = cartService.removeItem(itemId, userId);
                return ResponseEntity.ok(cart);
        }

        @Operation(summary = "Remove all items from the cart")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
                        @ApiResponse(responseCode = "404", description = "Cart not found")
        })
        @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.user.id")
        @DeleteMapping("/items")
        public ResponseEntity<CartResponse> clearCart(
                        @Parameter(description = "Identifier of the user who owns the cart", example = "12") @PathVariable("userId") Long userId) {
                CartResponse cart = cartService.clearCart(userId);
                return ResponseEntity.ok(cart);
        }

}
