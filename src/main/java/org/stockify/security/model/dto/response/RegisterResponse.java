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
@Schema(name = "RegisterResponse", description = "Response with JWT token, user profile and roles")
public class RegisterResponse {
    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Created user profile")
    private UserResponse user;

    @Schema(description = "Assigned role names")
    private Set<String> roles;
}

