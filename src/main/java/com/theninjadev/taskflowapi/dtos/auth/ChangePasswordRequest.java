package com.theninjadev.taskflowapi.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters")
    private String newPassword;
}
