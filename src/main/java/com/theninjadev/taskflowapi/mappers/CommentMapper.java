package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.comment.CommentDto;
import com.theninjadev.taskflowapi.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.fullName", target = "authorFullName")
    CommentDto toDto(Comment comment);
}