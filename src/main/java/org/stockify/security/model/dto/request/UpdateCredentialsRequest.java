package org.stockify.security.model.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCredentialsRequest {

    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Email(message = "El correo electrónico no es válido")
    private String newEmail;

    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String newPassword;

    private String currentPassword;

    @AssertTrue(message = "Current password is required to update credentials")
    public boolean isUpdateRequestValid() {
        boolean hasNewEmail = newEmail != null && !newEmail.isBlank();
        boolean hasNewPassword = newPassword != null && !newPassword.isBlank();
        if (!hasNewEmail && !hasNewPassword) {
            return false;
        }
        return currentPassword != null && !currentPassword.isBlank();
    }
}