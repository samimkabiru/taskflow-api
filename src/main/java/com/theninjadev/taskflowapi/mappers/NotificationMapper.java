package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.notification.NotificationDto;
import com.theninjadev.taskflowapi.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "recipient.id", target = "recipientId")
    NotificationDto toDto(Notification notification);
}