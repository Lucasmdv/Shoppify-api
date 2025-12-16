package org.stockify.model.mapper;

import org.mapstruct.Mapper;
import org.stockify.dto.response.PaymentDetailResponse;
import org.stockify.model.entity.PaymentDetailEntity;

@Mapper(componentModel = "spring")
public interface PaymentDetailMapper {
    PaymentDetailResponse toDto(PaymentDetailEntity entity);
}
