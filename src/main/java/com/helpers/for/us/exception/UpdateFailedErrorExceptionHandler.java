package com.you.and.me.exception;

public class UpdateFailedErrorExceptionHandler extends RuntimeException{

    public UpdateFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
