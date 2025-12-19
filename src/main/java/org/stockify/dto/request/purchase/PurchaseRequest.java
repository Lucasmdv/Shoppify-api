package org.stockify.dto.request.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.stockify.dto.request.transaction.TransactionRequest;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseRequest {

    @Schema(description = "Identifier of the provider", example = "1")
    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @Schema(description = "Unit price negotiated for the purchase", example = "120.50", minimum = "0.0")
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    @Schema(description = "Transaction details associated with the purchase")
    @NotNull(message = "Transaction is required")
    @Valid
    private TransactionRequest transaction;
}
