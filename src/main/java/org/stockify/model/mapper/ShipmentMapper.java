package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.shipment.ShipmentRequest;
import org.stockify.dto.request.shipment.UpdateShipmentRequest;
import org.stockify.dto.response.ShipmentResponse;
import org.stockify.model.entity.SaleEntity;
import org.stockify.model.entity.ShipmentEntity;
import org.stockify.model.enums.OrderStatus;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", constant = "PROCESSING")
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "sale", source = "sale")
    @Mapping(target = "shipmentCost", ignore = true)
    ShipmentEntity toEntity(ShipmentRequest dto, SaleEntity sale);

    @Mapping(target = "saleId", source = "sale.id")
    ShipmentResponse toResponseDTO(ShipmentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "sale", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateOrderEntity(UpdateShipmentRequest dto, @MappingTarget ShipmentEntity entity);

    @Named("stringToOrderStatus")
    default OrderStatus stringToOrderStatus(String status) {
        return status == null ? null : OrderStatus.valueOf(status);
    }
}