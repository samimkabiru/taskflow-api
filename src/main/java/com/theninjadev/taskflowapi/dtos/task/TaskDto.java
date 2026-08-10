package com.theninjadev.taskflowapi.dtos.task;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        UUID boardId,
        UUID taskListId,
        String shortCode,
        String title,
        String description,
        double position,
        String priority,
        LocalDate dueDate,
        UUID assigneeId,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}