package org.stockify.dto.request.store;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.stockify.dto.shared.HomeCarouselItem;

public record StoreRequest(
                @Schema(description = "Name of the store", example = "Main Street Store")
                @NotBlank(message = "Store name is required")
                @Size(min = 3, max = 15, message = "Store name must be between 3 and 15 characters")
                String storeName,

                @Schema(description = "Store address", example = "123 Main St")
                @NotBlank(message = "Address is required")
                @Size(max = 100, message = "Address must be less than or equal to 100 characters")
                String address,

                @Schema(description = "City where the store is located", example = "New York")
                @NotBlank(message = "City is required")
                @Size(min = 3, max = 100, message = "City must be between 3 and 100 characters")
                String city,

                @Schema(description = "Phone of the store", example = "223-4817825")
                @NotBlank(message = "Phone is required.")
                @Size(min = 3, max = 20, message = "Phone must be between 3 and 20 characters")
                String phone,

                @Schema(description = "Postal Code", example = "1900")
                @NotBlank(message = "Postal code is required")
                @Size(max = 20, message = "Postal code must be less than or equal to 20 characters")
                String postalCode,

                @Schema(description = "Facebook page URL", example = "https://facebook.com/shoppify")
                @Size(min = 3, max = 100, message = "Facebook URL must be between 3 and 100 characters") String facebook,

                @Schema(description = "Instagram profile URL", example = "https://instagram.com/shoppify")  
                @Size(min = 3, max = 100, message = "Instagram URL must be between 3 and 100 characters") String instagram,

                @Schema(description = "X (Twitter) profile URL", example = "https://x.com/shoppify")
                @Size(min = 3, max = 100, message = "X/Twitter URL must be between 3 and 100 characters") String twitter,

                @Schema(description = "Optional home carousel items (url, title)")
                List<HomeCarouselItem> homeCarousel,

                @Schema(description = "Shipping cost for small packages (<= 4 items)")
                @DecimalMin(value = "0.0", message = "Shipping cost must be zero or positive")
                Double shippingCostSmall,

                @Schema(description = "Shipping cost for medium packages (> 4 and <= 6 items)")
                @DecimalMin(value = "0.0", message = "Shipping cost must be zero or positive")
                Double shippingCostMedium,

                @Schema(description = "Shipping cost for large packages (> 6 items)")
                @DecimalMin(value = "0.0", message = "Shipping cost must be zero or positive")
                Double shippingCostLarge) {
}
