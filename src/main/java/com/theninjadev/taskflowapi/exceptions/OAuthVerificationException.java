package com.theninjadev.taskflowapi.exceptions;

public class OAuthVerificationException extends RuntimeException {
    public OAuthVerificationException() {
        super("Could not verify identity with Google.");
    }
}
