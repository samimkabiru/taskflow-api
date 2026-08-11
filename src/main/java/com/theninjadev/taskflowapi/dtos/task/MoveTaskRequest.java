package com.theninjadev.taskflowapi.dtos.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MoveTaskRequest {
    @NotNull(message = "Target list is required")
    private UUID taskListId;

    @NotNull(message = "Position is required")
    private Double position;
}