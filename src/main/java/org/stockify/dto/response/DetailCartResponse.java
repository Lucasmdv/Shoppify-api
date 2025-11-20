package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DetailCartResponse {

    @Schema(description = "Identifier of the cart item", example = "22")
    private Long id;

    @Schema(description = "Quantity added to the cart", example = "3")
    private Long quantity;

    @Schema(description = "Subtotal calculated for this item", example = "149.97")
    private BigDecimal subtotal;

    @Valid
    @Schema(description = "Product information associated with this cart item")
    private ProductResponseTransaction product;
}
