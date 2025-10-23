package org.stockify.security.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.stockify.dto.response.UserResponse;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginResponse", description = "Response with JWT token, sanitized user profile, and roles")
public class LoginResponse {
    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Authenticated user profile (sanitized)")
    private UserResponse user;

    @Schema(description = "Assigned role names")
    private Set<String> roles;

    // No permits field as requested
}
