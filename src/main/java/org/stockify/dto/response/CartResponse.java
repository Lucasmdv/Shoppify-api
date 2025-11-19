package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class CartResponse {

    @Schema(description = "Identifier of the cart", example = "5")
    private Long id;

    @Schema(description = "Calculated total for all the items inside the cart", example = "1299.90")
    private BigDecimal total;

    @Schema(description = "Items included in the cart")
    private Set<DetailCartResponse> items;
}
