package org.stockify.dto.request.notifications;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Builder;
import org.stockify.model.enums.NotificationType;

@Builder
public record NotificationRequest(
        @Schema(description = "Type of notification", example = "PERSONAL")
        @NotNull(message = "Notification type is required")
        NotificationType type,

        @Schema(description = "Short title to display", example = "Price drop!")
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title cannot exceed 120 characters")
        String title,

        @Schema(description = "Notification message to show the user", example = "The product is back in stock")
        @NotBlank(message = "Message is required")
        @Size(max = 500, message = "Message cannot exceed 500 characters")
        String message,

        @Schema(description = "Icon identifier or URL", example = "bell")
        @Size(max = 120, message = "Icon cannot exceed 120 characters")
        String icon,

        @Schema(description = "User id for PERSONAL notifications", example = "42")
        Long targetUserId,

        @Schema(description = "Product id related to the notification", example = "150")
        Long relatedProductId,

        @Schema(description = "Sale id related to the notification", example = "451")
        Long relatedSaleId,

        @Schema(description = "When the notification should be published (UTC)", example = "2025-12-31T12:00:00Z")
        Instant publishAt,

        @Schema(description = "Expiration timestamp (UTC)", example = "2025-12-31T23:59:59Z")
        Instant expiresAt
) {}
