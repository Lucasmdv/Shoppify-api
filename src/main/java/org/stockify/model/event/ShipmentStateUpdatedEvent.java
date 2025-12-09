package org.stockify.model.event;

import org.stockify.model.enums.OrderStatus;

public record ShipmentStateUpdatedEvent(Long shipmentId, OrderStatus oldState, OrderStatus newState) {}