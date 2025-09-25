package org.stockify.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.stockify.dto.request.transaction.TransactionCreatedRequest;
import org.stockify.dto.request.transaction.TransactionRequest;

import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.entity.TransactionEntity;

@Mapper(componentModel = "spring", uses = {ProviderMapper.class, DetailTransactionMapper.class})
public interface TransactionMapper {
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", ignore = true)
    TransactionEntity toEntity(TransactionRequest transactionRequest);

    TransactionEntity toEntity(TransactionCreatedRequest transactionCreatedRequest);

    @Mapping(target = "storeName", source = "store.storeName")
    TransactionResponse toDto(TransactionEntity transactionEntity);



    @Mapping(target = "type", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TransactionEntity partialUpdate(TransactionRequest transactionRequest, @MappingTarget TransactionEntity transactionEntity);
}
