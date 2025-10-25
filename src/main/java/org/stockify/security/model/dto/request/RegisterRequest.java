package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.stockify.dto.request.user.UserRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Schema(name = "RegisterRequest", description = "Composite request to register user profile and credentials together")
public class RegisterRequest {
    @Schema(description = "User profile data")
    private UserRequest user;

    @Schema(description = "Credentials data")
    private CredentialRequest credentials;
}

