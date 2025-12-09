package org.stockify.model.event;

import java.math.BigDecimal;

public record ProductDiscountUpdatedEvent(Long productId , String productName , BigDecimal oldDiscount, BigDecimal discount) {
}
