package com.you.and.me.exception;

public class InternalServerErrorExceptionHandler extends RuntimeException{
    public InternalServerErrorExceptionHandler(String message) {
        super(message);
    }
}
