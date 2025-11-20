package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.cart.CartRequest;
import org.stockify.dto.response.CartResponse;
import org.stockify.dto.response.DetailCartResponse;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.CartItemEntity;
import org.stockify.util.PriceCalculator;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, PriceCalculator.class})
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    CartEntity toEntity(CartRequest dto);

    @Mapping(target = "items", source = "products")
    @Mapping(target = "total", source = ".", qualifiedByName = "cartTotal")
    CartResponse toResponse(CartEntity entity);

    @Mapping(target = "product", source = "product")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "cartSubtotal")
    DetailCartResponse toDetailCartResponse(CartItemEntity entity);

    @Mapping(target = "id", ignore = true)
    CartEntity updateEntityFromRequest(CartRequest dto, @MappingTarget CartEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    void patchEntityFromRequest(CartRequest dto, @MappingTarget CartEntity entity);

}
