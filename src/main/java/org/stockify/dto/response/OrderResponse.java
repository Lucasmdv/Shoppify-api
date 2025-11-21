package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class OrderResponse {
    @Schema(description = "Unique identifier of the order", example = "1")
    Long id;

    @Schema(description = "Status of the order", example = "DELIVERED")
    private String status;

    @Schema(description = "Start date of the order", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "End date of the order", example = "2025-01-01")
    private LocalDate endDate;

    @Schema(description = "Sale details of the order")
    SaleResponse sale;

    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;
}