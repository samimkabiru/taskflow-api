package com.theninjadev.taskflowapi.exceptions;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("Uploaded file is empty.");
    }
}