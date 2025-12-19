package org.stockify.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "UserRequest", description = "Request body for creating or updating a User.")
public class UserRequest {


    @Schema(description = "User's image", example = "http://image.com/avatar.png", maxLength = 255)
    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    private String img;

    @NotBlank
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "First name must contain only letters")
    @Schema(description = "User's first name", example = "Maria", minLength = 2, maxLength = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Last name must contain only letters")
    @Schema(description = "User's last name", example = "Rodriguez", minLength = 2, maxLength = 20)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$", message = "DNI must contain exactly 8 digits")
    @Schema(description = "User's DNI (National ID Number)", example = "33445566", minLength = 8, maxLength = 8)
    private String dni;

    @NotBlank
    @Pattern(regexp = "^[0-9]{7,20}$", message = "Phone number must contain only numbers (7 to 20 digits)")
    @Schema(description = "User's phone number", example = "5491165439876", minLength = 7, maxLength = 20)
    private String phone;
}
