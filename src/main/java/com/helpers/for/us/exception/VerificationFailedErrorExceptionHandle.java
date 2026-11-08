package com.you.and.me.exception;

public class VerificationFailedErrorExceptionHandle extends RuntimeException{
    public VerificationFailedErrorExceptionHandle(String message) {
        super(message);
    }
}
