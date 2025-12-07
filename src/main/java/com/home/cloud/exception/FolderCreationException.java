package com.home.cloud.exception;

public class FolderCreationException extends  RuntimeException{
    public FolderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}