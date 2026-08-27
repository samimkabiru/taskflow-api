package com.theninjadev.taskflowapi.exceptions;

public class InviteNotPendingException extends RuntimeException {
    public InviteNotPendingException() {
        super("This invite is no longer pending and can't be acted on.");
    }
}