package com.theninjadev.taskflowapi.exceptions;

public class InviteEmailMismatchException extends RuntimeException {
    public InviteEmailMismatchException() {
        super("This invite was sent to a different email address than your account.");
    }
}
