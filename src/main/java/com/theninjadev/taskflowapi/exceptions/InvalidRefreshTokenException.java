package com.theninjadev.taskflowapi.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token.");
    }
}
