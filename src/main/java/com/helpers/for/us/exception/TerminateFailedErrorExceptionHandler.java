package com.you.and.me.exception;

public class TerminateFailedErrorExceptionHandler extends RuntimeException{
    public TerminateFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
