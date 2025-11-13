package org.stockify.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.stockify.dto.shared.HomeCarouselItem;

public record StoreRequest(
        @Schema(description = "Name of the store", example = "Main Street Store")
        @NotBlank(message = "Store name is required")
        @Size(max = 15, message = "Store name must be less than 15 characters")
        String storeName,

        @Schema(description = "Store address", example = "123 Main St")
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must be less than 255 characters")
        String address,

        @Schema(description = "City where the store is located", example = "New York")
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must be less than 100 characters")
        String city,


        @Schema(description = "Phone of the store", example = "223-4817825")
        @NotBlank(message = "Phone is required.")
        String phone,

        @Schema(description = "Facebook page URL", example = "https://facebook.com/shoppify")
        @Size(max = 255, message = "Facebook URL must be less than 255 characters")
        String facebook,

        @Schema(description = "Instagram profile URL", example = "https://instagram.com/shoppify")
        @Size(max = 255, message = "Instagram URL must be less than 255 characters")
        String instagram,

        @Schema(description = "X (Twitter) profile URL", example = "https://x.com/shoppify")
        @Size(max = 255, message = "X/Twitter URL must be less than 255 characters")
        String twitter,

        @Schema(description = "Optional home carousel items (url, title)")
        List<HomeCarouselItem> homeCarousel
) {}
