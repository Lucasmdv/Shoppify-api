package org.stockify.model.event;

public record ProductStockUpdatedEvent (Long productId,String productName,Long oldStock ,Long newStock){}