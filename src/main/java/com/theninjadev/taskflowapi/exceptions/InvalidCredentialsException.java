package com.theninjadev.taskflowapi.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials.");
    }
}
