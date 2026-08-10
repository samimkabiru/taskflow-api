package com.theninjadev.taskflowapi.dtos.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be under 255 characters")
    private String title;

    private String description;
    private LocalDate dueDate;
    private String priority;   // optional — validated against the enum in the service, null is allowed
    private UUID assigneeId;   // optional
}