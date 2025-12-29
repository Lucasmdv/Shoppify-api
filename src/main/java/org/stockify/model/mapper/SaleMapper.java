package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.SaleResponse;
import org.stockify.model.entity.SaleEntity;

@Mapper(componentModel = "spring", uses = { TransactionMapper.class, ShipmentMapper.class })
public interface SaleMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "id", ignore = true)
    SaleEntity toEntity(SaleRequest dto);

    @Mapping(target = "userDni", source = "user.dni")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "transaction", source = "transaction")
    @Mapping(target = "shipment", source = "shipment")
    SaleResponse toResponseDTO(SaleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateSaleEntity(SaleRequest saleRequest, @MappingTarget SaleEntity saleEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateSaleEntity(SaleRequest saleRequest, @MappingTarget SaleEntity saleEntity);
}
