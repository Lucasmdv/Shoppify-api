package org.stockify.util;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.CartItemEntity;
import org.stockify.model.entity.DetailTransactionEntity;
import org.stockify.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    /**
     * Calcula el precio final de un producto después de aplicar su porcentaje de descuento.
     * Se asume que los valores de price y discountPercentage ya están limpios y validados.
     */
    @Named("discountPrice")
    public BigDecimal calculateDiscountPrice(ProductEntity product) {

        BigDecimal price = product.getPrice();
        BigDecimal discountPercentage = product.getDiscountPercentage();

        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }

        BigDecimal discountFactor = discountPercentage.divide(
                BigDecimal.valueOf(100),
                4,
                RoundingMode.HALF_UP
        );

        BigDecimal finalFactor = BigDecimal.ONE.subtract(discountFactor);
        BigDecimal finalPrice = price.multiply(finalFactor);

        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal calculateSubtotal(BigDecimal discountPrice, Long quantity){
        if (discountPrice == null || quantity == null) return BigDecimal.ZERO;
        return discountPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Named("detailSubtotal")
    public BigDecimal calculateSubtotalDiscount(DetailTransactionEntity detail){
        if (detail == null) return BigDecimal.ZERO;
        ProductEntity product = detail.getProduct();
        Long quantity = detail.getQuantity();
        if (product == null || quantity == null) return BigDecimal.ZERO;
        return calculateDiscountPrice(product).multiply(BigDecimal.valueOf(quantity));
    }

    @Named("cartSubtotal")
    public BigDecimal calculateCartSubtotal(CartItemEntity item){
        if (item == null) return BigDecimal.ZERO;
        ProductEntity product = item.getProduct();
        Long quantity = item.getQuantity();
        if (product == null || quantity == null) return BigDecimal.ZERO;
        return calculateDiscountPrice(product).multiply(BigDecimal.valueOf(quantity));
    }

    @Named("cartTotal")
    public BigDecimal calculateCartTotal(CartEntity cart){
        if (cart.getProducts() == null) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemEntity item : cart.getProducts()) {
            total = total.add(calculateCartSubtotal(item));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
