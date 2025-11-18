package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.cart.CartRequest;
import org.stockify.dto.request.category.CategoryRequest;
import org.stockify.dto.response.CartResponse;
import org.stockify.dto.response.CategoryResponse;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    CartEntity toEntity(CartRequest dto);

    CartResponse toResponse(CartEntity entity);


    @Mapping(target = "id", ignore = true)
    CartEntity updateEntityFromRequest(CartRequest dto, @MappingTarget CartEntity entity);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
            , nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    void patchEntityFromRequest(CartRequest dto, @MappingTarget CartEntity entity);

}
