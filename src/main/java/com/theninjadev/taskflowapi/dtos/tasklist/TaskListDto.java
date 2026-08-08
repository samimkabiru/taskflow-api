package com.theninjadev.taskflowapi.dtos.tasklist;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskListDto(
        UUID id,
        UUID boardId,
        String title,
        double position,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
