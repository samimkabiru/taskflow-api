package com.theninjadev.taskflowapi.dtos.user;

public record GetAvatarResult(
        byte[] bytes,
        String contentType
) {}
