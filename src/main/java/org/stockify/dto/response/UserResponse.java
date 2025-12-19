package org.stockify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse extends RepresentationModel<UserResponse> {

    @Schema(description = "Unique identifier of the user", example = "123")
    private Long id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's DNI (national identity document number)", example = "12345678")
    private String dni;

    @Schema(description = "User's phone number", example = "+541112345678")
    private String phone;

    @Schema(description = "User's image", example = "http://image.com/avatar.png", maxLength = 255)
    private String img;

    @Schema(description = "Date of registration", example = "2021-01-01")
    private String dateOfRegistration;

    @Schema(description = "User's email", example = "asd@asd")
    private String email;

    @Schema(description = "User's cart")
    private CartResponse cart;
}
