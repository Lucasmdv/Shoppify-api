package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponse {
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long id;

    @Schema(description = "Status of the order", example = "DELIVERED")
    private String status;

    @Schema(description = "Start date of the order", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "End date of the order", example = "2025-01-01")
    private LocalDate endDate;

    @Schema(description = "Sale id of the order")
    private Long saleId;

    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;

    @Schema(description = "Street were the shipment will be delivered")
    private String street;

    @Schema(description = "Number of street were the shipment will be delivered")
    private Integer number;

    @Schema(description = "City were the shipment will be delivered")
    private String city;

    @Schema(description = "Postal Code were the shipment will be delivered")
    private Integer zip;

    @Schema(description = "Notes of the shipment")
    private String notes;
}