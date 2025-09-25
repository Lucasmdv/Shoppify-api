package org.stockify.model.mapper;

import org.mapstruct.*;
import org.stockify.dto.request.user.UserRequest;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfRegistration", ignore = true)
    UserEntity toEntity(UserRequest clientRequest);
    @Mapping(target = "links", ignore = true)
    UserResponse toDto(UserEntity clientEntity);

    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfRegistration", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateClientEntity(UserRequest clientRequest, @MappingTarget UserEntity clientEntity);

    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfRegistration", ignore = true)
    void updateClientEntity(UserRequest clientRequest, @MappingTarget UserEntity clientEntity);
}