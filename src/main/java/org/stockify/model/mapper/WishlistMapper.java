/*package org.stockify.model.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stockify.dto.response.WishlistResponse;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.entity.WishlistItem;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface WishlistMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "products", source = "items")
    WishlistResponse toResponse(UserEntity user, List<WishlistItem> items);

    default org.stockify.model.entity.ProductEntity map(WishlistItem item) {
        return item != null ? item.getProduct() : null;
    }
}*/
