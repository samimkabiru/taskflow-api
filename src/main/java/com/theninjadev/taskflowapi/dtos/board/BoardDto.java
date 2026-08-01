package com.theninjadev.taskflowapi.dtos.board;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BoardDto(
        UUID id,
        String name,
        String description,
        String accentColor,
        UUID ownerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
