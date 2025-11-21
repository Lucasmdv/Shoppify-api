package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.transaction.TransactionRequest;

@Builder
@Getter
public class OrderRequest {
    @NotNull(message = "Sale is required")
    @Valid
    @Schema(description = "Sale details for the order")
    private SaleRequest sale;

    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;
}