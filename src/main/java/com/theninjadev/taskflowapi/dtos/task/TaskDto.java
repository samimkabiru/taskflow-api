package com.theninjadev.taskflowapi.dtos.task;

import com.theninjadev.taskflowapi.dtos.label.LabelDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
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
        Set<LabelDto> labels,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}