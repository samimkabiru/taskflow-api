package com.theninjadev.taskflowapi.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException() {
        super("Notification not found.");
    }
}
