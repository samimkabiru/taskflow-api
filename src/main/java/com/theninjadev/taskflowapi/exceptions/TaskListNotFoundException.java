package com.theninjadev.taskflowapi.exceptions;

public class TaskListNotFoundException extends RuntimeException {
    public TaskListNotFoundException() {
        super("Task list not found.");
    }
}