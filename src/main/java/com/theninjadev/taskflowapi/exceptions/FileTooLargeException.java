package com.theninjadev.taskflowapi.exceptions;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException() {
        super("File exceeds the maximum allowed size.");
    }
}