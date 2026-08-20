package com.theninjadev.taskflowapi.exceptions;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException() {
        super("Attachment not found.");
    }
}