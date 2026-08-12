package com.theninjadev.taskflowapi.exceptions;

public class LabelNotOnBoardException extends RuntimeException {
    public LabelNotOnBoardException() {
        super("This label does not belong to the same board as the task.");
    }
}