package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.product.ProductCSVRequest;
import org.stockify.dto.request.product.ProductRequest;
import org.stockify.dto.response.ProductResponse;
import org.stockify.model.entity.CategoryEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.ProviderEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "categories", target = "categories", qualifiedByName = "entitiesToNames")
    @Mapping(source = "providers", target = "providers", qualifiedByName = "providerEntitiesToIds")
    @Mapping(target = "stock", expression = "java(entity.getStock() == null ? 0L : entity.getStock())")
    @Mapping(target = "soldQuantity", expression = "java(entity.getSoldQuantity() == null ? 0L : entity.getSoldQuantity())")
    @Mapping(target = "discountPercentage", expression = "java(toDouble(entity.getDiscountPercentage()))")
    @Mapping(target = "priceWithDiscount", expression = "java(calculatePriceWithDiscount(entity.getPrice(), entity.getDiscountPercentage()))")
    ProductResponse toResponse(ProductEntity entity);

    @Mapping(target = "detailTransactions", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "stock", expression = "java(normalizeStock(request.stock()))")
    @Mapping(target = "soldQuantity", expression = "java(initialSoldQuantity())")
    ProductEntity toEntity(ProductRequest request);

    /**
     * PUT: actualiza todo (incluso campos nulos)
     */
    default void updateEntityFromRequest(ProductRequest dto, @MappingTarget ProductEntity entity) {
        updateFromRequest(dto, entity);
        if (dto.stock() != null) {
            entity.setStock(normalizeStock(dto.stock()));
        }
    }

    /**
     * Lógica interna del update (excepto categorías)
     */
    @Mapping(target = "detailTransactions", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "soldQuantity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ProductRequest dto, @MappingTarget ProductEntity entity);

    /**
     * PATCH: ignora campos nulos, actualiza los que vienen con datos
     */
    @Mapping(target = "detailTransactions", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "soldQuantity", ignore = true)
    void patchEntityFromRequest(ProductRequest dto, @MappingTarget ProductEntity entity);

    @Named("entitiesToNames")
    default Set<String> entitiesToNames(Set<CategoryEntity> categories) {
        if (categories == null || categories.isEmpty()) return new LinkedHashSet<>();
        return categories.stream()
                .map(CategoryEntity::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Named("providerEntitiesToIds")
    default Set<Long> providerEntitiesToIds(Set<ProviderEntity> providers) {
        if (providers == null || providers.isEmpty()) return new LinkedHashSet<>();
        return providers.stream()
                .map(ProviderEntity::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "detailTransactions", ignore = true)
    @Mapping(target = "stock", expression = "java(normalizeStock(dto.getStock()))")
    @Mapping(target = "soldQuantity", expression = "java(initialSoldQuantity())")
    ProductEntity toEntity(ProductCSVRequest dto);

    @Mapping(target = "categories", source = "categories", qualifiedByName = "stringToCategorySet")
    ProductRequest toRequest(ProductCSVRequest dto);

    @Named("stringToCategorySet")
    static Set<String> mapCategories(String value) {
        if (value == null || value.isBlank()) return new LinkedHashSet<>();
        return Stream.of(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    default Long normalizeStock(Long stock) {
        return stock == null ? 0L : stock;
    }

    default Long initialSoldQuantity() {
        return 0L;
    }

    @AfterMapping
    default void normalizeMappedValues(ProductRequest source, @MappingTarget ProductEntity target) {
        if (source == null || target == null) {
            return;
        }

        if (source.price() != null) {
            target.setPrice(applyMoneyScale(source.price()));
        }

        if (source.unitPrice() != null) {
            target.setUnitPrice(applyMoneyScale(source.unitPrice()));
        }

        if (source.discountPercentage() != null) {
            target.setDiscountPercentage(normalizeDiscountPercentage(source.discountPercentage()));
        } else if (target.getDiscountPercentage() == null) {
            target.setDiscountPercentage(normalizeDiscountPercentage(null));
        }
    }

    @AfterMapping
    default void normalizeMappedValues(ProductCSVRequest source, @MappingTarget ProductEntity target) {
        if (source == null || target == null) {
            return;
        }

        target.setPrice(applyMoneyScale(source.getPrice()));
        target.setUnitPrice(applyMoneyScale(source.getUnitPrice()));
        target.setDiscountPercentage(normalizeDiscountPercentage(null));
    }

    default BigDecimal normalizeDiscountPercentage(BigDecimal discount) {
        if (discount == null) {
            return clampDiscount(BigDecimal.ZERO);
        }
        return clampDiscount(discount);
    }

    default double toDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    default double calculatePriceWithDiscount(BigDecimal price, BigDecimal discount) {
        BigDecimal basePrice = price == null ? BigDecimal.ZERO : price;
        BigDecimal normalizedDiscount = normalizeDiscountPercentage(discount);
        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                normalizedDiscount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        BigDecimal finalPrice = basePrice.multiply(discountFactor);
        return finalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static BigDecimal applyMoneyScale(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal clampDiscount(BigDecimal discount) {
        if (discount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal scaled = discount.setScale(2, RoundingMode.HALF_UP);
        if (scaled.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (scaled.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);
        }
        return scaled;
    }
}
