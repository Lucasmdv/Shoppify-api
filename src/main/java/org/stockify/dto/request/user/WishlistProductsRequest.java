package org.stockify.dto.request.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistProductsRequest {

    @NotEmpty(message = "At least one product id must be provided")
    private Set<Long> productIds = new LinkedHashSet<>();
}
