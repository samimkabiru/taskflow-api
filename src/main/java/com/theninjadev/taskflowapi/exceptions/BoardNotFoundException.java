package com.theninjadev.taskflowapi.exceptions;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException() {
        super("Board not found.");
    }
}
