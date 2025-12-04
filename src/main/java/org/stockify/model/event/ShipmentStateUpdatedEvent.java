package org.stockify.model.event;

public record ShipmentStateUpdatedEvent(Long shipmentId, String oldState, String newState) {}