package com.you.and.me.exception;

public class UserRegisterFailedErrorExceptionHandler extends RuntimeException{
    public UserRegisterFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
