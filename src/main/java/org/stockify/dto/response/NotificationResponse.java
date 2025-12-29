package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.stockify.model.enums.NotificationType;

@Schema(description = "Notification response DTO")
public record NotificationResponse(
        @Schema(description = "Notification identifier", example = "101")
        Long id,

        @Schema(description = "Short title to display", example = "Price drop!")
        String title,

        @Schema(description = "Message content", example = "The product you follow is back in stock")
        String message,

        @Schema(description = "Notification type", example = "PRODUCT_ALERT")
        NotificationType type,

        @Schema(description = "Icon identifier or URL", example = "bell")
        String icon,

        @Schema(description = "Related product id when applies", example = "150")
        Long relatedProductId,

        @Schema(description = "Related sale id when applies", example = "1001")
        Long relatedSaleId,

        @Schema(description = "When the notification becomes visible (UTC)", example = "2025-12-31T12:00:00Z")
        Instant publishAt,

        @Schema(description = "When the notification expires (UTC)", example = "2025-12-31T23:59:59Z")
        Instant expiresAt,

        @Schema(description = "Creation timestamp", example = "2025-10-21T18:30:00Z")
        Instant createdAt,

        @Schema(description = "Whether the user hid the notification", example = "false")
        boolean hidden,

        @Schema(description = "Whether the user has read it", example = "false")
        boolean read
) {}
