package com.theninjadev.taskflowapi.dtos.auth;

public record GoogleTokenInfo(String aud, String email, String email_verified, String name) {}
