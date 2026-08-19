package com.theninjadev.taskflowapi.mappers;

import com.theninjadev.taskflowapi.dtos.attachment.AttachmentDto;
import com.theninjadev.taskflowapi.entities.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "uploadedBy.id", target = "uploadedBy")
    AttachmentDto toDto(Attachment attachment);
}