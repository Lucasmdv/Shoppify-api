package org.stockify.dto.request.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for {@link org.stockify.model.entity.DetailTransactionEntity}
 */
@Value
@Getter
@Setter
@Valid
public class DetailTransactionRequest {
    @Schema(description = "ID of the product", example = "123")
    @NotNull(message = "Product ID is required")
    Long productID;

    @Schema(description = "Quantity of the product in the transaction (must be positive)", example = "10", minimum = "1")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    Long quantity;
}
