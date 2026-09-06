package com.theninjadev.taskflowapi.dtos.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 255, message = "Full name must be under 255 characters")
    private String fullName;
}
