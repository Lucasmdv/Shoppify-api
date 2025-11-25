package org.stockify.model.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecifications {

    public static Specification<ProductEntity> byName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            String pattern = name + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.get("name"))),
                pattern
            );
        };
    }

    public static Specification<ProductEntity> byProvider(String provider) {
        return (root, query, cb) -> {
            if (provider == null || provider.isBlank()) return null;
            String pattern = provider + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.join("providers").get("name"))),
                pattern
            );
        };
    }

    public static Specification<ProductEntity> byCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;
            String pattern = category + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.join("categories").get("name"))),
                pattern
            );
        };
    }

    public static Specification<ProductEntity> bySku(String sku) {
        return (root, query, cb) -> {
            if (sku == null || sku.isBlank()) return null;
            String pattern = sku + "%";
            return cb.like(
                    cb.function("unaccent", String.class, cb.lower(root.get("sku"))),
                    pattern
            );
        };
    }

    public static Specification<ProductEntity> byBarCode(String barCode) {
        return (root, query, cb) -> {
            if (barCode == null || barCode.isBlank()) return null;
            String pattern = barCode + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.get("barcode"))),
                pattern
            );
        };
    }

    public static Specification<ProductEntity> byCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.or(categories.stream()
                    .map(category -> {
                        String pattern = category + "%";
                        return cb.like(
                            cb.function("unaccent", String.class, cb.lower(root.join("categories").get("name"))),
                            pattern
                        );
                    })
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
                    .map(provider -> {
                        String pattern = provider + "%";
                        return cb.like(
                            cb.function("unaccent", String.class, cb.lower(root.join("providers").get("name"))),
                            pattern
                        );
                    })
                    .toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<ProductEntity> byBrand(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isBlank()) return null;
            String pattern = brand + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.get("brand"))),
                pattern
            );
        };
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

    public static Specification<ProductEntity> byDiscount(Double discount) {
        return (root, query, cb) -> discount == null ? null : cb.equal(root.get("discountPercentage"), BigDecimal.valueOf(discount));
    }

    public static Specification<ProductEntity> byDiscountGreaterThan(Double discount) {
        return (root, query, cb) -> discount == null ? null : cb.greaterThan(root.get("discountPercentage"), BigDecimal.valueOf(discount));
    }

    public static Specification<ProductEntity> byDiscountLessThan(Double discount) {
        return (root, query, cb) -> discount == null ? null : cb.lessThan(root.get("discountPercentage"), BigDecimal.valueOf(discount));
    }

    public static Specification<ProductEntity> byDiscountBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null || max == null) {
                return null;
            }
            return cb.between(root.get("discountPercentage"), BigDecimal.valueOf(min), BigDecimal.valueOf(max));
        };
    }

    public static Specification<ProductEntity> byDescription(String description) {
        return (root, query, cb) -> {
            if (description == null || description.isBlank()) return null;
            String pattern = description + "%";
            return cb.like(
                cb.function("unaccent", String.class, cb.lower(root.get("description"))),
                pattern
            );
        };
    }

    public static Specification<ProductEntity> byStock(Long stock) {
        return (root, query, cb) -> stock == null ? null : cb.equal(root.get("stock"), stock);
    }

    public static Specification<ProductEntity> byStockLessThan(Long stockLessThan) {
        return (root, query, cb) -> stockLessThan == null ? null : cb.lt(root.get("stock"), stockLessThan);
    }

    public static Specification<ProductEntity> byStockGreaterThan(Long stockGreaterThan) {
        return (root, query, cb) -> stockGreaterThan == null ? null : cb.gt(root.get("stock"), stockGreaterThan);
    }

    public static Specification<ProductEntity> byStockBetween(Long min, Long max) {
        return (root, query, cb) -> {
            if (min == null || max == null) {
                return null;
            }
            return cb.between(root.get("stock"), min, max);
        };
    }

    public static Specification<ProductEntity> byProductOrCategories(String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank()) {
                return null;
            }

            query.distinct(true);
            String pattern = value + "%";
            Join<ProductEntity, ?> categoriesJoin = root.join("categories", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.function("unaccent", String.class, cb.lower(root.get("name"))), pattern),
                    cb.like(cb.function("unaccent", String.class, cb.lower(categoriesJoin.get("name"))), pattern)
            );
        };
    }

    public static Specification<ProductEntity> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

}
