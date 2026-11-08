package com.you.and.me.exception;

public class UnAuthenticateErrorExceptionHandler extends RuntimeException{
    public UnAuthenticateErrorExceptionHandler(String message) {
        super(message);
    }
}
