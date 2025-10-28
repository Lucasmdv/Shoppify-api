package org.stockify.model.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.ProductEntity;

import java.util.List;

public class ProductSpecifications {

    public static Specification<ProductEntity> byName(String name) {
        return (root, query, cb) -> {
            if (name == null) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ProductEntity> byProvider(String provider) {
        return (root, query, cb) -> {
            if (provider == null) return null;
            return cb.like(cb.lower(root.join("providers").get("name")), "%" + provider.toLowerCase() + "%");
        };
    }

    public static Specification<ProductEntity> byCategory(String category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            return cb.like(cb.lower(root.join("categories").get("name")), "%" + category.toLowerCase() + "%");
        };
    }

    public static Specification<ProductEntity> bySku(String sku) {
        return (root, query, cb) ->
                sku == null ? null : cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%");
    }

    public static Specification<ProductEntity> byBarCode(String barCode) {
        return (root, query, cb) ->
                barCode == null ? null : cb.like(cb.lower(root.get("barcode")), "%" + barCode.toLowerCase() + "%");
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
        return (root, query, cb) ->
                brand == null ? null : cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
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
        return (root, query, cb) ->
                description == null ? null : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
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
            String pattern = "%" + value.toLowerCase() + "%";
            Join<ProductEntity, ?> categoriesJoin = root.join("categories", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(categoriesJoin.get("name")), pattern)
            );
        };
    }
}
