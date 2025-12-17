package org.stockify.model.event;

import org.stockify.model.enums.PaymentStatus;

public record PaymentStatusUpdatedEvent(
        Long saleId,
        PaymentStatus oldStatus,
        PaymentStatus newStatus,
        Long userId
) {}
