package com.theninjadev.taskflowapi.exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException() {
        super("An account with this email already exists.");
    }

    public UserExistsException(String message) {
        super(message);
    }
}
