package org.stockify.security.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response object returned after successful authentication.
 * Contains the JWT token that should be used for subsequent authenticated requests.
 */
@Schema(name = "AuthResponse", description = "DTO returned after successful authentication, containing the JWT token and optional message.")
public record AuthResponse(

        @Schema(description = "JWT token issued for the authenticated session.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Optional message about the authentication or update result.", example = "Credenciales actualizadas correctamente")
        String message

) {
    /**
     * Creates a new builder for AuthResponse
     * 
     * @return A new AuthResponseBuilder instance
     */
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    /**
     * Builder class for creating AuthResponse instances
     */
    public static class AuthResponseBuilder {
        private String token;
        private String message;

        /**
         * Sets the token value for the AuthResponse
         * 
         * @param token The JWT token to include in the response
         * @return This builder for method chaining
         */
        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * Sets an optional message for response
         * @param message Optional message to include in the response
         * @return Builder with optional message
         */
        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Builds the AuthResponse with the configured values
         * 
         * @return A new AuthResponse instance
         */
        public AuthResponse build() {
            return new AuthResponse(token,message);
        }
    }
}
