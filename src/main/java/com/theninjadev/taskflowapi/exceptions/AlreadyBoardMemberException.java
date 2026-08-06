package com.theninjadev.taskflowapi.exceptions;

public class AlreadyBoardMemberException extends RuntimeException {
    public AlreadyBoardMemberException() {
        super("This user is already a member of this board.");
    }
}
