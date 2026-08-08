package com.theninjadev.taskflowapi.dtos.tasklist;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReorderTaskListRequest {
    @NotNull(message = "Position is required")
    private Double position;
}