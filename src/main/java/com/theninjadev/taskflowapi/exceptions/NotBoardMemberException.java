package com.theninjadev.taskflowapi.exceptions;

public class NotBoardMemberException extends RuntimeException {
    public NotBoardMemberException() {
        super("You are not a member of this board.");
    }
}
