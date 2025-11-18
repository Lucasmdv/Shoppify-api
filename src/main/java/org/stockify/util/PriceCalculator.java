package org.stockify.util;

import org.springframework.stereotype.Component;
import org.stockify.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO_MONEY = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public BigDecimal calculateDiscountPrice(ProductEntity product) {
        if (product == null) {
            return ZERO_MONEY;
        }
        return calculatePriceWithDiscount(product.getPrice(), product.getDiscountPercentage());
    }

    public BigDecimal calculatePriceWithDiscount(BigDecimal price, BigDecimal discount) {
        BigDecimal basePrice = price == null ? BigDecimal.ZERO : price;
        BigDecimal normalizedDiscount = normalizeDiscountPercentage(discount);

        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ZERO_MONEY;
        }

        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                normalizedDiscount.divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP)
        );
        return basePrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal normalizeMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal normalizeMoneyOrZero(BigDecimal value) {
        BigDecimal normalized = normalizeMoney(value);
        return normalized == null ? ZERO_MONEY : normalized;
    }

    public BigDecimal normalizeDiscountPercentage(BigDecimal discount) {
        if (discount == null) {
            return ZERO_MONEY;
        }

        BigDecimal scaled = discount.setScale(2, RoundingMode.HALF_UP);
        if (scaled.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO_MONEY;
        }
        if (scaled.compareTo(ONE_HUNDRED) > 0) {
            return ONE_HUNDRED.setScale(2, RoundingMode.HALF_UP);
        }
        return scaled;
    }

    public double toDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    public double discountPercentageAsDouble(BigDecimal discount) {
        return toDouble(normalizeDiscountPercentage(discount));
    }

    public double priceWithDiscountAsDouble(ProductEntity product) {
        return toDouble(calculateDiscountPrice(product));
    }

    public double priceWithDiscountAsDouble(BigDecimal price, BigDecimal discount) {
        return toDouble(calculatePriceWithDiscount(price, discount));
    }

    public Long normalizeStock(Long stock) {
        return stock == null ? 0L : stock;
    }

    public Long initialSoldQuantity() {
        return 0L;
    }

    public BigDecimal calculateSubtotal(BigDecimal discountPrice, Long quantity){
        if (discountPrice == null || quantity == null) return BigDecimal.ZERO;
        return discountPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
