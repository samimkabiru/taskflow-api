package com.theninjadev.taskflowapi.exceptions;

public class AssigneeNotBoardMemberException extends RuntimeException {
    public AssigneeNotBoardMemberException() {
        super("The assignee must be a member of this board.");
    }
}