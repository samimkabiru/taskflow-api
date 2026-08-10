package com.theninjadev.taskflowapi.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Task not found.");
    }
}