package org.stockify.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProviderRequest {

    @Schema(description = "Contact person's name", example = "John Supplier", minLength = 3, maxLength = 100)
    @NotBlank(message = "Contact name is required")
    @Size(min = 3, max = 100)
    private String name;

    @Schema(description = "Phone number", example = "+1-555-1234567", maxLength = 255)
    @Size(max = 255, message = "Phone cannot exceed 255 characters")
    private String phone;

    @Schema(description = "Email address", example = "contact@example.com", maxLength = 255)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Schema(description = "Tax Identification Number (e.g., VAT number)", example = "12345678", maxLength = 100)
    @NotBlank(message = "Tax ID is required")
    @Size(max = 100, message = "Tax ID cannot exceed 100 characters")
    private String taxId;

    @Schema(description = "Tax address", example = "123 Supplier St, City", maxLength = 255)
    @NotBlank(message = "Tax address is required")
    @Size(max = 255, message = "Tax address cannot exceed 255 characters")
    private String taxAddress;

    @Schema(description = "Registered business name", example = "Supplier Inc.", maxLength = 255)
    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name cannot exceed 255 characters")
    private String businessName;
}
