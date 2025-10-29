package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "User profile data is required")
    @Valid
    private UserRequest user;

    @Schema(description = "Credentials data")
    @NotNull(message = "Credentials data is required")
    @Valid
    private CredentialRequest credentials;
}
