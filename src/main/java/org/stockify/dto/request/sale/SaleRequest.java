package org.stockify.dto.request.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.stockify.dto.request.transaction.TransactionRequest;

@NoArgsConstructor
@Getter
@Setter
public class SaleRequest {
    @NotNull(message = "User identifier is required")
    @Schema(description = "ID of the user", example = "10")
    Long userId;

    @NotNull(message = "Transaction is required")
    @Valid
    @Schema(description = "Transaction details for the sale")
    TransactionRequest transaction;
}
