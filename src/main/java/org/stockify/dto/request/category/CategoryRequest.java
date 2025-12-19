package org.stockify.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "CategoryRequest", description = "Request body for creating or updating a category.")
public class CategoryRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(
            description = "Name of the category",
            example = "Electronics",
            minLength = 2,
            maxLength = 50,
            required = true
    )
    private String name;

    @Schema(description = "Image URL for the category", example = "https://example.com/category.png", maxLength = 200)
    @Size(max = 200, message = "Image URL must be at most 200 characters")
    @Pattern(regexp = "^(?=\\S)(.*\\S)?$", message = "Image URL cannot be blank")
    private String imgUrl;
}
