package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.transaction.TransactionRequest;

@Builder
public class OrderRequest {
    @NotNull(message = "Sale is required")
    @Valid
    @Schema(description = "Sale details for the order")
    SaleRequest sale;
}