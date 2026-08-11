package com.theninjadev.taskflowapi.exceptions;

public class TaskListNotOnBoardException extends RuntimeException {
    public TaskListNotOnBoardException() {
        super("This task list is not on this board.");
    }
}
