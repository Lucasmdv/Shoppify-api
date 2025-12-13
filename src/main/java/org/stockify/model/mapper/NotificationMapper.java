package org.stockify.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.dto.response.NotificationResponse;
import org.stockify.model.entity.NotificationEntity;
import org.stockify.model.projections.NotificationSummary;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    NotificationEntity toEntity(NotificationRequest request);

    @Mapping(target = "read", constant = "false")
    @Mapping(target = "hidden", constant = "false")
    NotificationResponse toResponse(NotificationEntity entity);

    @Mapping(target = "read", source = "read")
    @Mapping(target = "hidden", source = "hidden")
    NotificationResponse toResponse(NotificationSummary summary);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(NotificationRequest request, @MappingTarget NotificationEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntityFromRequest(NotificationRequest request, @MappingTarget NotificationEntity entity);
}
