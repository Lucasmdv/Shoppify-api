package org.stockify.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    @Schema(description = "Edit status of the order")
    private String status;

    @Schema(description = "Edit end date of order")
    private LocalDate endDate;
}