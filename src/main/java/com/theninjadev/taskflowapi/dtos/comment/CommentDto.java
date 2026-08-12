package com.theninjadev.taskflowapi.dtos.comment;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID taskId,
        UUID authorId,
        String authorFullName,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}