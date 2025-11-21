package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class UpdateOrderRequest {
    @Schema(description = "Edit status of the order")
    private String status;

    @Schema(description = "Edit end date of order")
    private LocalDate endDate;

    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;
}