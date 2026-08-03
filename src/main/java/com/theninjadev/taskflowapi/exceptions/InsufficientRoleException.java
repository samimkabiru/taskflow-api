package com.theninjadev.taskflowapi.exceptions;

public class InsufficientRoleException extends RuntimeException {
    public InsufficientRoleException() {
        super("You do not have permission to perform this action.");
    }
}
