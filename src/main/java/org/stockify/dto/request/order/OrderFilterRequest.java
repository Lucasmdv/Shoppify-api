package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {
    @Schema(description = "Filter by ID of the order", example = "1")
    private Long orderId;

    @Schema(description = "Filter by ID of the client", example = "1")
    private Long clientId;

    @Schema(description = "Filter by status of the order", example = "DELIVERED")
    private String status;

    @Schema(description = "Filter by start date", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "Filter by end date", example = "2025-01-01")
    private LocalDate endDate;

    @Schema(description = "Filter by min price", example = "10")
    private Double minPrice;

    @Schema(description = "Filter by max price", example = "500")
    private Double maxPrice;

    @Schema(description = "Filter by pickup in store or not")
    private Boolean pickup;
}