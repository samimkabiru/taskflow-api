package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.task.TaskDto;
import com.theninjadev.taskflowapi.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = LabelMapper.class)
public interface TaskMapper {

    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "taskList.id", target = "taskListId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "createdBy.id", target = "createdBy")
    TaskDto toDto(Task task);
}