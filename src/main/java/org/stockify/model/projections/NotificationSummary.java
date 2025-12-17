package org.stockify.model.projections;

import java.time.Instant;
import org.stockify.model.enums.NotificationType;

public interface NotificationSummary {
    Long getId();
    String getTitle();
    String getMessage();
    NotificationType getType();
    Instant getPublishAt();
    Instant getCreatedAt();
    Boolean getRead();
    Boolean getHidden();
    String getIcon();
    Long getRelatedProductId();
    Long getRelatedSaleId();
}
