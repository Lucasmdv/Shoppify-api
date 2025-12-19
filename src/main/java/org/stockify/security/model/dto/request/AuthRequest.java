package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(name = "AuthRequest", description = "DTO used for user authentication containing email and password.")
public record AuthRequest(

        @Schema(description = "User email address used to authenticate.", example = "user@example.com", maxLength = 255)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        @Schema(description = "User password in plain text (must be encrypted before storage).", example = "P@ssw0rd", minLength = 8, maxLength = 100)
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password

) {}
