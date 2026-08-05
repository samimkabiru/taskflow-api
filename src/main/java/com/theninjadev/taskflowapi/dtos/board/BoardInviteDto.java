package com.theninjadev.taskflowapi.dtos.board;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BoardInviteDto(
        UUID id,
        UUID boardId,
        String email,
        String role,
        String status,
        OffsetDateTime createdAt
) {}
