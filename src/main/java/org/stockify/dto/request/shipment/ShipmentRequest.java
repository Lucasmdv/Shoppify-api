package org.stockify.dto.request.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    @Schema(description = "Boolean for pickup in store or not")
    private Boolean pickup;

    @Schema(description = "Street were the shipment will be delivered", minLength = 3, maxLength = 120)
    private String street;

    @Schema(description = "Number of street were the shipment will be delivered", example = "123", minimum = "1")
    private Integer number;

    @Schema(description = "City were the shipment will be delivered", minLength = 2, maxLength = 100)
    private String city;

    @Schema(description = "Postal Code were the shipment will be delivered", example = "1900", minimum = "1")
    private Integer zip;

    @Schema(description = "Notes of the shipment", maxLength = 100)
    @Size(max = 100, message = "Notes must be at most 100 characters")
    private String notes;

    @AssertTrue(message = "Delivery address is required when pickup is false")
    public boolean isDeliveryAddressValid() {
        if (Boolean.TRUE.equals(pickup)) {
            return true;
        }

        if (isBlank(street) || isBlank(city)) {
            return false;
        }

        if (street.trim().length() < 3 || street.trim().length() > 120) {
            return false;
        }

        if (city.trim().length() < 2 || city.trim().length() > 100) {
            return false;
        }

        if (number == null || number <= 0) {
            return false;
        }

        if (zip == null || zip <= 0) {
            return false;
        }

        return true;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
