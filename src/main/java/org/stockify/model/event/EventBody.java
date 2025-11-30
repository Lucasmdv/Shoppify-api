package org.stockify.model.event;

import org.stockify.model.enums.NotificationType;

public record EventBody(String title,String message,NotificationType type, String icon) {
}
