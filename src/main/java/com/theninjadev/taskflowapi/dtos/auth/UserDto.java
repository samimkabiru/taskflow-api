package com.theninjadev.taskflowapi.dtos.auth;

import lombok.Data;

@Data
public class UserDto {
    private String fullName;
    private String email;
    private String avatarUrl;

}
