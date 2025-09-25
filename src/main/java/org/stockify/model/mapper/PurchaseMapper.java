package org.stockify.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.stockify.dto.request.purchase.PurchaseRequest;
import org.stockify.dto.response.PurchaseResponse;
import org.stockify.model.entity.PurchaseEntity;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class, ProviderMapper.class})
public interface PurchaseMapper {

    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "providerId", target = "provider.id")
    PurchaseEntity toEntity(PurchaseRequest dto);

    @Mapping(target = "transaction", source = "transaction")
    PurchaseResponse toResponseDTO(PurchaseEntity entity);

    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdatePurchaseEntity(PurchaseRequest request, @MappingTarget PurchaseEntity entity);
}
