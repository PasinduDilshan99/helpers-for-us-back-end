package com.you.and.me.exception;

public class NotFoundErrorExceptionHandler extends RuntimeException{
    public NotFoundErrorExceptionHandler(String message) {
        super(message);
    }
}
