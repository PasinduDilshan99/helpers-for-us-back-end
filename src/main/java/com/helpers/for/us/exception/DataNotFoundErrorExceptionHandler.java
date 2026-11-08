package com.you.and.me.exception;

public class DataNotFoundErrorExceptionHandler extends RuntimeException{
    public DataNotFoundErrorExceptionHandler(String message) {
        super(message);
    }
}
