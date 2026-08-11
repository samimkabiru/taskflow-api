package com.theninjadev.taskflowapi.dtos.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLabelRequest {
    @NotBlank(message = "Label name is required")
    @Size(max = 50, message = "Label name must be under 50 characters")
    private String name;

    @NotBlank(message = "Color is required")
    private String color;
}