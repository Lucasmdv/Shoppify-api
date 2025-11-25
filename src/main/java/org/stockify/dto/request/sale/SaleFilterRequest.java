package org.stockify.dto.request.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SaleFilterRequest {
    @Schema(description = "Filter by sale ID", example = "1")
    private Long saleId;

    @Schema(description = "Filter by user ID", example = "10")
    private Long userId;

    @Schema(description = "Filter by transaction ID", example = "100")
    private Long transactionId;

    @Schema(description = "Filter by start date", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "Filter by end date", example = "2025-12-31")
    private LocalDate endDate;

    @Schema(description = "Filter by payment method", example = "CASH")
    private String paymentMethod;

    @Schema(description = "Filter by min price", example = "10")
    private Double minPrice;

    @Schema(description = "Filter by max price", example = "500")
    private Double maxPrice;
}
