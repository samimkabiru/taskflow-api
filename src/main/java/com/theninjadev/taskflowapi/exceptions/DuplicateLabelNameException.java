package com.theninjadev.taskflowapi.exceptions;

public class DuplicateLabelNameException extends RuntimeException {
    public DuplicateLabelNameException() {
        super("A label with this name already exists on this board.");
    }
}