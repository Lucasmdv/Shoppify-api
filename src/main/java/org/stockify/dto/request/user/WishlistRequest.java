package org.stockify.dto.request.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.stockify.dto.response.ProductResponse;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WishlistRequest {
    @NotNull(message = "User identifier is required")
    private Long userId;
    @NotEmpty(message = "The wishlist must contain at least one product")
    @NotNull(message = "Products are required")
    private Set<Long> productsIds = new HashSet<>();

}
