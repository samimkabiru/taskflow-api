package com.theninjadev.taskflowapi.exceptions;

public class InviteAlreadyPendingException extends RuntimeException {
    public InviteAlreadyPendingException() {
        super("There is already a pending invite for this email on this board.");
    }
}
