package org.stockify.dto.request.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.stockify.dto.request.shipment.ShipmentRequest;
import org.stockify.dto.request.transaction.TransactionRequest;

@NoArgsConstructor
@Getter
@Setter
public class SaleRequest {
    @Schema(description = "ID of the user", example = "10")
    Long userId;

    @NotNull(message = "Transaction is required")
    @Valid
    @Schema(description = "Transaction details for the sale")
    TransactionRequest transaction;

    @Schema(description = "Details of the shipment")
    @Valid
    ShipmentRequest shipment;
}
