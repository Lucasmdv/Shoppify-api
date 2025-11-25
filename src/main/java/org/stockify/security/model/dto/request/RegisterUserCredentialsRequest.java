package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(name = "RegisterUserCredentialsRequest", description = "DTO for registering a user profile together with authentication credentials in a single step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserCredentialsRequest {

    // Credentials
    @Schema(description = "Unique username of the user", example = "john_doe")

    private String username;


    @Schema(description = "User email address", example = "john@example.com")
    private String email;

    @Schema(description = "Raw password to be encoded before storage", example = "P@ssw0rd123")
    private String password;

    // User profile
    @Schema(description = "User's image", example = "http://image.com/avatar.png")
    private String img;

    @Schema(description = "User's first name", example = "María")
    private String firstName;

    @Schema(description = "User's last name", example = "Rodríguez")
    private String lastName;

    @Schema(description = "User's DNI (National ID Number)", example = "33445566")
    private String dni;

    @Schema(description = "User's phone number", example = "+54 9 11 6543-9876")
    private String phone;
}
