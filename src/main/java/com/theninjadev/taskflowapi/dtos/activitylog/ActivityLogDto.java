package com.theninjadev.taskflowapi.dtos.activitylog;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record ActivityLogDto(
        UUID id,
        UUID boardId,
        UUID taskId,
        UUID actorId,
        String actorFullName,
        String actionType,
        Map<String, Object> metadata,
        OffsetDateTime createdAt
) {}