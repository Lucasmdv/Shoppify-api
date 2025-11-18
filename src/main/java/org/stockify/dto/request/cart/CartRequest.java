package org.stockify.dto.request.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    @Schema(description = "Identifier of the client that owns the cart", example = "12")
    @NotNull(message = "Client identifier is required")
    private Long clientId;

    @Schema(description = "Items that should be stored inside the cart")
    @Valid
    @NotEmpty(message = "The cart must contain at least one item")
    private List<DetailCartRequest> items;
}
