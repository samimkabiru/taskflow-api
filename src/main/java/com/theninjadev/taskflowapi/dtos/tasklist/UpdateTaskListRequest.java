package com.theninjadev.taskflowapi.dtos.tasklist;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTaskListRequest {
    @Size(max = 255, message = "Title must be under 255 characters")
    private String title;
}