package org.stockify.dto.request.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.stockify.dto.request.sale.SaleRequest;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;

    @NotNull(message = "Adress is required")
    @Schema(description = "Adress were the shipment will be delivered")
    private String adress;
}