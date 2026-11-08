package com.you.and.me.exception;

public class InsertFailedErrorExceptionHandler extends RuntimeException{
    public InsertFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
