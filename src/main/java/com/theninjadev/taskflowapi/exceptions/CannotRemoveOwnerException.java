package com.theninjadev.taskflowapi.exceptions;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException() {
        super("The board owner cannot be removed or have their role changed this way.");
    }
}
