package org.stockify.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.response.DetailTransactionResponse;
import org.stockify.dto.response.ProductResponseTransaction;
import org.stockify.model.entity.DetailTransactionEntity;
import org.stockify.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface DetailTransactionMapper {

    @Mapping(target = "product", expression = "java(toProductResponse(entity.getProduct()))")
    DetailTransactionResponse toDto(DetailTransactionEntity entity);
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    DetailTransactionEntity toEntity(DetailTransactionRequest request);

    default ProductResponseTransaction toProductResponse(ProductEntity product) {
        if (product == null) {
            return null;
        }
        BigDecimal basePrice = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
        BigDecimal discount = normalizeDiscount(product.getDiscountPercentage());
        BigDecimal finalPrice = calculatePriceWithDiscount(basePrice, discount);

        return new ProductResponseTransaction(
                product.getId(),
                product.getName(),
                basePrice,
                discount,
                finalPrice,
                product.getBarcode()
        );
    }

    private BigDecimal calculatePriceWithDiscount(BigDecimal price, BigDecimal discount) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal factor = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal result = price.multiply(factor);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeDiscount(BigDecimal discount) {
        if (discount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.valueOf(100);
        BigDecimal value = discount;
        if (value.compareTo(min) < 0) {
            value = min;
        }
        if (value.compareTo(max) > 0) {
            value = max;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
