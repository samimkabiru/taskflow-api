package com.theninjadev.taskflowapi.dtos.auth;

public record AuthResult(
        String accessToken,
        String refreshToken,
        UserDto user
) {}