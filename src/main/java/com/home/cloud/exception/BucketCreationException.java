package com.home.cloud.exception;

public class BucketCreationException extends  RuntimeException{
    public BucketCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
