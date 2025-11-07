package org.stockify.dto.shared;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Carousel item shown on the home page",
        example = "{\n  \"id\": 10,\n  \"url\": \"https://cdn.example.com/hero1.jpg\",\n  \"title\": \"Big Summer Sale\",\n  \"href\": \"/sale\"\n}")
public record HomeCarouselItem(
        @Schema(description = "Unique identifier of the carousel entry", example = "10",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Absolute or relative image URL", example = "https://example.com/img1.jpg")
        String url,

        @Schema(description = "Display title for the slide", example = "Big Summer Sale")
        String title,

        @Schema(description = "Link to navigate when selected (absolute or app route)", example = "/sale")
        String href
) {}
