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
@Schema(name = "LoginResponse", description = "Response with JWT token, sanitized user profile, and permits")
public class LoginResponse {
    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Authenticated user profile (sanitized)")
    private UserResponse user;

    @Schema(description = "Assigned permits (uppercase enum names, ordered, without duplicates)")
    private Set<String> permits;

    @Schema(description = "Assigned roles (uppercase names, ordered, without duplicates)")
    private Set<String> roles;

    @Schema(description = "Enviopack access token")
    private String envioPackToken;
}
