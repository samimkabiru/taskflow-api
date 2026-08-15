package com.theninjadev.taskflowapi.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank(message = "Comment content is required")
    private String content;
}