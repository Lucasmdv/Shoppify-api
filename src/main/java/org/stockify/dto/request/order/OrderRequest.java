package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.stockify.dto.request.transaction.TransactionRequest;

@Builder
public class OrderRequest {
    @NotNull(message = "Client ID is required")
    @Schema(description = "ID of the client", example = "1")
    Long clientId;

    @NotNull(message = "Transaction is required")
    @Valid
    @Schema(description = "Transaction details for the sale")
    TransactionRequest transaction;
}