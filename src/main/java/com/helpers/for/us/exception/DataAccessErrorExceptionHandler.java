package com.you.and.me.exception;

public class DataAccessErrorExceptionHandler extends RuntimeException{
    public DataAccessErrorExceptionHandler(String message) {
        super(message);
    }
}
