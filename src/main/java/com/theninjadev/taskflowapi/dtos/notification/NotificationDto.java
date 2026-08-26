package com.theninjadev.taskflowapi.dtos.notification;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID recipientId,
        String type,
        Map<String, Object> payload,
        boolean isRead,
        OffsetDateTime createdAt
) {}