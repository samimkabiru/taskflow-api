package com.theninjadev.taskflowapi.dtos.board;

import com.theninjadev.taskflowapi.enums.BoardRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BoardMemberDto(
        UUID id,
        UUID userId,
        String userFullName,
        String userEmail,
        BoardRole role,
        OffsetDateTime joinedAt
) {}
