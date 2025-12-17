package org.stockify.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        String token,
        @JsonProperty("expires_in") long expiresIn) {
}
