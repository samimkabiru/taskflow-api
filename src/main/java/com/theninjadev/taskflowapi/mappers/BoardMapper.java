package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.entities.Board;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    BoardDto toDto(Board board);
}
