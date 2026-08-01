package com.theninjadev.taskflowapi.dtos.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBoardRequest {
    @NotBlank(message = "Board name is required")
    @Size(max = 255, message = "Board name must be under 255 characters")
    private String name;

    private String description;

    @NotBlank(message = "Accent color is required")
    private String accentColor;

    @NotBlank(message = "Task prefix is required")
    @Pattern(regexp = "^[A-Z]{2,6}$", message = "Task prefix must be 2-6 uppercase letters")
    private String taskPrefix;
}
