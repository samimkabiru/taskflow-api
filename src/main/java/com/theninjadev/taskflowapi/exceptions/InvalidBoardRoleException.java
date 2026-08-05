package com.theninjadev.taskflowapi.exceptions;

public class InvalidBoardRoleException extends RuntimeException {
    public InvalidBoardRoleException() {
        super("Invalid role: must be ADMIN, MEMBER, or VIEWER.");
    }
}
