package org.stockify.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Set;

@Builder
public record ProductRequest(
        @Schema(description = "Name of the product", example = "Monitor 144hz", minLength = 2, maxLength = 50)
        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @Schema(description = "Url from the product", example = "http://image.com", maxLength = 255)
        @Size(max = 255, message = "Image URL cannot exceed 255 characters")
        String imgURL,

        @Schema(description = "Detailed description of the product", example = "Latest model with advanced features", maxLength = 500)
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Schema(description = "Price of the product", example = "999.99", minimum = "0.0")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
        BigDecimal price,

        @Schema(description = "Unit price of the product", example = "899.99", minimum = "0.0")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
        BigDecimal unitPrice,

        @Schema(description = "Discount percentage applied to the price", example = "15.0", minimum = "0.0", maximum = "100.0")
        @DecimalMin(value = "0.0", message = "Discount cannot be negative")
        @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
        BigDecimal discountPercentage,

        @Schema(description = "Available stock quantity", example = "100", minimum = "0")
        @NotNull
        @Min(value = 0, message = "Stock cannot be negative")
        Long stock,

        @Schema(description = "SKU code (Stock Keeping Unit)", example = "PHONE-001", minLength = 6, maxLength = 12)
        @NotBlank
        @Size(min = 6, max = 12)
        String sku,

        @Schema(description = "Barcode of the product", example = "012345678901", minLength = 12, maxLength = 12)
        @NotBlank
        @Size(min = 12, max = 12)
        String barcode,

        @Schema(description = "Brand name of the product", example = "TechBrand", minLength = 2, maxLength = 50)
        @NotBlank
        @Size(min = 2, max = 50)
        String brand,

        @Schema(description = "Set of categories the product belongs to", example = "[\"Electronics\", \"Mobile\"]")
        @NotEmpty(message = "At least one category is required")
        Set<@NotBlank String> categories,

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
