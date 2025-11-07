package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import org.stockify.dto.shared.HomeCarouselItem;

@Schema(description = "Store information")
public record StoreResponse(
        @Schema(description = "Unique identifier of the store", example = "1")
        Long id,

        @Schema(description = "Name of the store", example = "Main Street Store")
        String storeName,

        @Schema(description = "Store address", example = "123 Main St, Downtown")
        String address,

        @Schema(description = "City where the store is located", example = "New York")
        String city,

        @Schema(description = "Phone of the store", example = "223-4817825")
        String phone,

        @Schema(description = "Facebook page URL", example = "https://facebook.com/shoppify")
        String facebook,

        @Schema(description = "Instagram profile URL", example = "https://instagram.com/shoppify")
        String instagram,

        @Schema(description = "X (Twitter) profile URL", example = "https://x.com/shoppify")
        String twitter,

        @Schema(description = "Home page carousel items")
        List<HomeCarouselItem> homeCarousel
) {}
