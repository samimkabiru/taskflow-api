package com.theninjadev.taskflowapi.dtos.label;

import java.util.UUID;

public record LabelDto(
        UUID id,
        UUID boardId,
        String name,
        String color
) {}