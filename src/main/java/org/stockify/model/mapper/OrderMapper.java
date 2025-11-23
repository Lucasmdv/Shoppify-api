package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.order.OrderRequest;
import org.stockify.dto.request.order.UpdateOrderRequest;
import org.stockify.dto.response.OrderResponse;
import org.stockify.model.entity.OrderEntity;
import org.stockify.model.enums.OrderStatus;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    OrderEntity toEntity(OrderRequest dto);

    @Mapping(target = "saleId", source = "sale.id")
    OrderResponse toResponseDTO(OrderEntity entity);

    @Named("stringToOrderStatus")
    default OrderStatus stringToOrderStatus(String status) {
        return status == null ? null : OrderStatus.valueOf(status);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "sale", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    @Mapping(target = "endDate", source = "endDate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateOrderEntity(UpdateOrderRequest dto, @MappingTarget OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "sale", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    @Mapping(target = "endDate", source = "endDate")
    void updateOrderEntity(UpdateOrderRequest dto, @MappingTarget OrderEntity entity);
}