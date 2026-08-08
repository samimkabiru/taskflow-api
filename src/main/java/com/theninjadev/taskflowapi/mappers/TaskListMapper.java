package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.tasklist.TaskListDto;
import com.theninjadev.taskflowapi.entities.TaskList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskListMapper {

    @Mapping(source = "board.id", target = "boardId")
    TaskListDto toDto(TaskList taskList);
}