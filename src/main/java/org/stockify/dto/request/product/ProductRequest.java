package org.stockify.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Set;

@Builder
public record ProductRequest(
        @Schema(description = "Name of the product", example = "Monitor 144hz", minLength = 1, maxLength = 50)
        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @Schema(description = "Url from the product", example = "http://image.com")
        String imgURL,

        @Schema(description = "Detailed description of the product", example = "Latest model with advanced features")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Schema(description = "Price of the product", example = "999.99")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
        BigDecimal price,

        @Schema(description = "Unit price of the product", example = "899.99")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
        BigDecimal unitPrice,

        @Schema(description = "Discount percentage applied to the price", example = "15.0")
        @DecimalMin(value = "0.0", message = "Discount cannot be negative")
        @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
        BigDecimal discountPercentage,

        @Schema(description = "Available stock quantity", example = "100")
        @NotNull
        @Min(value = 0, message = "Stock cannot be negative")
        Long stock,

        @Schema(description = "SKU code (Stock Keeping Unit)", example = "PHONE-001")
        @NotBlank
        @Size(min = 6, max = 12)
        String sku,

        @Schema(description = "Barcode of the product", example = "0123456789012")
        @NotBlank
        @Size(min = 12, max = 12)
        String barcode,

        @Schema(description = "Brand name of the product", example = "TechBrand")
        @NotBlank
        @Size(min = 2, max = 50)
        String brand,

        @Schema(description = "Set of categories the product belongs to", example = "[\"Electronics\", \"Mobile\"]")
        @NotEmpty(message = "At least one category is required")
        Set<String> categories,

        @Schema(description = "Indicates if the product is inactive", example = "false")
        Boolean inactive
    ) {

    public ProductRequest {
        if (discountPercentage == null) {
            discountPercentage = BigDecimal.ZERO;
        }
        if (inactive == null) {
            inactive = false;
        }
    }
}
