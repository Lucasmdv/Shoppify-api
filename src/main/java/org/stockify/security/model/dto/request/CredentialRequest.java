package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO used to receive user credential information during authentication or registration requests.
 */
@Getter
@Setter
@ToString(exclude = "password")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CredentialRequest", description = "Request body for user credential operations such as login or signup.")
public class CredentialRequest {

    @Schema(description = "Unique username of the user.", example = "john_doe", minLength = 4, maxLength = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username must contain only letters, numbers, dots, hyphens or underscores")
    private String username;

    @Schema(description = "User password (must be encrypted before storing).", example = "P@ssw0rd123", minLength = 8, maxLength = 100)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Schema(description = "User email address used for communication or account recovery.", example = "john@example.com", maxLength = 255)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    // Roles and permits are managed separately; not part of basic credential input
}
