package com.theninjadev.taskflowapi.exceptions;

public class InvalidImageTypeException extends RuntimeException {
    public InvalidImageTypeException() {
        super("Avatar must be a JPEG, PNG, or WebP image.");
    }
}