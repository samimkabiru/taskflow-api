package com.theninjadev.taskflowapi.exceptions;

public class InviteNotFoundException extends RuntimeException {
    public InviteNotFoundException() {
        super("Invite not found.");
    }
}
