package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.entities.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(source = "board.id", target = "boardId")
    LabelDto toDto(Label label);
}