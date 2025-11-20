package org.stockify.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stockify.dto.response.WishlistProductResponse;
import org.stockify.model.entity.WishlistProductEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface WishlistProductMapper {

    @Mapping(target = "product", source = "product")
    WishlistProductResponse toResponse(WishlistProductEntity wishlistProduct);

    default LocalDateTime map(Instant value) {
        return value == null ? null : LocalDateTime.ofInstant(value, ZoneOffset.UTC);
    }
}
