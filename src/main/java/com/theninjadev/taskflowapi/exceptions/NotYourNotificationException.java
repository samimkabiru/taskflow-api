package com.theninjadev.taskflowapi.exceptions;

public class NotYourNotificationException extends RuntimeException {
    public NotYourNotificationException() {
        super("You are not the owner of this notification.");
    }
}
