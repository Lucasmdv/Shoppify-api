package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(name = "RegisterCredentialsRequest", description = "DTO for registering user credentials and optionally linking to an existing user profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCredentialsRequest {

    @Schema(description = "Unique username of the user", example = "john_doe")
    private String username;

    @Schema(description = "User email address", example = "john@example.com")
    private String email;

    @Schema(description = "Raw password to be encoded before storage", example = "P@ssw0rd123")
    private String password;

    @Schema(description = "Optional ID of an existing user profile to link these credentials to", example = "42")
    private Long userId;
}
