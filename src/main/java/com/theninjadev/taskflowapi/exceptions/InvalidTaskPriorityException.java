package com.theninjadev.taskflowapi.exceptions;

public class InvalidTaskPriorityException extends RuntimeException {
    public InvalidTaskPriorityException() {
        super("Invalid priority: must be LOW, MEDIUM, HIGH, or URGENT.");
    }
}