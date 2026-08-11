package com.theninjadev.taskflowapi.dtos.label;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLabelRequest {
    @Size(max = 50, message = "Label name must be under 50 characters")
    private String name;

    private String color;
}