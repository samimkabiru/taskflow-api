package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.dtos.board.BoardMemberDto;
import com.theninjadev.taskflowapi.entities.Board;
import com.theninjadev.taskflowapi.entities.BoardMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    BoardDto toDto(Board board);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "role", target = "role")
    BoardMemberDto toDto(BoardMember boardMember);
}
