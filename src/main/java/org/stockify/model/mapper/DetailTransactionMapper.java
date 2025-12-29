package org.stockify.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.response.DetailTransactionResponse;
import org.stockify.model.entity.DetailTransactionEntity;
import org.stockify.util.PriceCalculator;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, PriceCalculator.class})
public interface DetailTransactionMapper {

    @Mapping(target = "product", source = "product")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "detailSubtotal")
    DetailTransactionResponse toDto(DetailTransactionEntity entity);
    @Mapping(target = "transaction", ignore = true)

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    DetailTransactionEntity toEntity(DetailTransactionRequest request);



}
