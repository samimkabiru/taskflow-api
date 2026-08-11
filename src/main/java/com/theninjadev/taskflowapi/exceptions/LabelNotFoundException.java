package com.theninjadev.taskflowapi.exceptions;

public class LabelNotFoundException extends RuntimeException {
    public LabelNotFoundException() {
        super("Label not found.");
    }
}