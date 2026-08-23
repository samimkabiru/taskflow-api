package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.activitylog.ActivityLogDto;
import com.theninjadev.taskflowapi.entities.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {
    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "actor.id", target = "actorId")
    @Mapping(source = "actor.fullName", target = "actorFullName")
    ActivityLogDto toDto(ActivityLog activityLog);
}