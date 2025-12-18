package org.stockify.dto.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Carousel item shown on the home page",
        example = "{\n  \"id\": 10,\n  \"url\": \"https://cdn.example.com/hero1.jpg\",\n  \"title\": \"Big Summer Sale\",\n  \"href\": \"/sale\"\n}")
public record HomeCarouselItem(
        @Schema(description = "Unique identifier of the carousel entry", example = "10",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Absolute or relative image URL", example = "https://example.com/img1.jpg")
        @Size(max = 512, message = "Image URL cannot exceed 512 characters")
        @Pattern(regexp = "^https?://.+", message = "Image URL must start with http:// or https://")
        String url,

        @Schema(description = "Display title for the slide", example = "Big Summer Sale")
        @Size(min = 4, max = 80, message = "Title must be between 4 and 80 characters")
        String title,

        @Schema(description = "Link to navigate when selected (absolute or app route)", example = "/sale")
        @Size(max = 512, message = "Link cannot exceed 512 characters")
        @Pattern(regexp = "^(\\/|https?://).+", message = "Link must start with / or http(s)://")
        String href
) {}
