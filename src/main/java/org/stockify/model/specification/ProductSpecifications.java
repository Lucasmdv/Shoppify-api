package org.stockify.model.specification;

import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecifications {

    public static Specification<ProductEntity> byName(String name) {
        return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<ProductEntity> byProvider(String provider) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.join("providers").get("name")), "%" + provider.toLowerCase() + "%");
    }

    public static Specification<ProductEntity> byCategory(String category) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.join("categories").get("name")), "%" + category.toLowerCase() + "%");
    }

    public static Specification<ProductEntity> bySku(String sku) {
        return (root, query, cb) -> cb.like(root.get("sku"), "%" + sku + "%");
    }

    public static Specification<ProductEntity> byBarCode(String barCode) {
        return (root, query, cb) -> cb.like(root.get("barcode"), "%" + barCode + "%");
    }

    public static Specification<ProductEntity> byCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.or(categories.stream()
                    .map(category -> cb.like(cb.lower(root.join("categories").get("name")), "%" + category.toLowerCase() + "%"))
                    .toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<ProductEntity> byProviders(List<String> providers) {
        if (providers == null || providers.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.or(providers.stream()
                    .map(provider -> cb.like(cb.lower(root.join("providers").get("name")), "%" + provider.toLowerCase() + "%"))
                    .toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<ProductEntity> byBrand(String brand) {
        return (root, query, cb) -> cb.like(root.get("brand"), "%" + brand + "%");
    }

    public static Specification<ProductEntity> byPrice(Double price) {
        return (root, query, cb) -> cb.equal(root.get("price"), price);
    }

    public static Specification<ProductEntity> byPriceGreaterThan(Double price) {
        return (root, query, cb) -> cb.greaterThan(root.get("price"), price);
    }

    public static Specification<ProductEntity> byPriceLessThan(Double price) {
        return (root, query, cb) -> cb.lessThan(root.get("price"), price);
    }

    public static Specification<ProductEntity> byPriceBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("price"), min, max);
    }

    public static Specification<ProductEntity> byDescription(String description) {
        return (root, query, cb) -> cb.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<ProductEntity> byStock(Double stock) {
        return (root, query, cb) -> cb.equal(root.get("stock"), toBigDecimal(stock));
    }

    public static Specification<ProductEntity> byStockLessThan(Double stockLessThan) {
        return (root, query, cb) -> cb.lessThan(root.get("stock"), toBigDecimal(stockLessThan));
    }

    public static Specification<ProductEntity> byStockGreaterThan(Double stockGreaterThan) {
        return (root, query, cb) -> cb.greaterThan(root.get("stock"), toBigDecimal(stockGreaterThan));
    }

    public static Specification<ProductEntity> byStockBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("stock"), toBigDecimal(min), toBigDecimal(max));
    }

    private static BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}
