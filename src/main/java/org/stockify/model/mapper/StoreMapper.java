package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.store.StoreRequest;
import org.stockify.dto.response.StoreResponse;
import org.stockify.model.entity.StoreEntity;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "homeCarousel", ignore = true)
    @Mapping(target = "id", ignore = true)
    StoreEntity toEntity(StoreRequest request);

    StoreResponse toResponse(StoreEntity entity);

    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "homeCarousel", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(StoreRequest request, @MappingTarget StoreEntity entity);
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "homeCarousel", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntityFromRequest(StoreRequest request, @MappingTarget StoreEntity entity);

    // Mapping helpers for carousel items
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "id", ignore = true)
    org.stockify.model.entity.CarouselItem toEmbeddable(org.stockify.dto.shared.HomeCarouselItem dto);
    org.stockify.dto.shared.HomeCarouselItem toDto(org.stockify.model.entity.CarouselItem emb);
}
