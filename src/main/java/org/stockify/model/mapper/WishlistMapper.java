package org.stockify.model.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stockify.dto.response.WishlistResponse;
import org.stockify.model.entity.WishlistEntity;
import org.stockify.model.mapper.WishlistProductMapper;

@Mapper(componentModel = "spring", uses = WishlistProductMapper.class)
public interface WishlistMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "products", source = "wishlistProducts")
    WishlistResponse toResponse(WishlistEntity wishlist);
}
