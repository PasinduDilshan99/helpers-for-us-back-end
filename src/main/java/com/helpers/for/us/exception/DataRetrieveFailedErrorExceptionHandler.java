package com.you.and.me.exception;

public class DataRetrieveFailedErrorExceptionHandler extends RuntimeException{
    public DataRetrieveFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
