package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.product.ProductCSVRequest;
import org.stockify.dto.request.product.ProductRequest;
import org.stockify.dto.response.ProductResponse;
import org.stockify.dto.response.ProductResponseTransaction;
import org.stockify.model.entity.CategoryEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.ProviderEntity;
import org.stockify.util.PriceCalculator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", uses = PriceCalculator.class)
public interface ProductMapper {

    @Mapping(source = "categories", target = "categories", qualifiedByName = "entitiesToNames")
    @Mapping(source = "providers", target = "providers", qualifiedByName = "providerEntitiesToIds")
    @Mapping(target = "stock")
    @Mapping(target = "soldQuantity")
    @Mapping(target = "discountPercentage")
    @Mapping(target = "priceWithDiscount", source = "entity", qualifiedByName = "discountPrice")
    ProductResponse toResponse(ProductEntity entity);

    @Mapping(target = "priceWithDiscount", source = "entity", qualifiedByName = "discountPrice")
    ProductResponseTransaction toTransactionProduct(ProductEntity entity);

    @Mapping(target = "detailTransactions", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "soldQuantity", ignore = true)
    ProductEntity toEntity(ProductRequest request);

    /**
     * PUT: actualiza todo (incluso campos nulos)
     */
    default void updateEntityFromRequest(ProductRequest dto, @MappingTarget ProductEntity entity) {
        updateFromRequest(dto, entity);
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
    @Mapping(target = "discountPercentage", ignore = true)
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

}
