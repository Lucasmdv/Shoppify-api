package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.order.OrderRequest;
import org.stockify.dto.request.order.UpdateOrderRequest;
import org.stockify.dto.response.OrderResponse;
import org.stockify.model.entity.OrderEntity;

@Mapper(componentModel = "spring", uses = {SaleMapper.class})
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    OrderEntity toEntity(OrderRequest dto);
    OrderResponse toResponseDTO(OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateOrderEntity(UpdateOrderRequest dto, @MappingTarget OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    void updateOrderEntity(UpdateOrderRequest dto, @MappingTarget OrderEntity entity);
}