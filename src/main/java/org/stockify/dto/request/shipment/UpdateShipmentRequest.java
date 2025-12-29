package org.stockify.dto.request.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShipmentRequest {
    @Schema(description = "Edit status of the order")
    private String status;
}