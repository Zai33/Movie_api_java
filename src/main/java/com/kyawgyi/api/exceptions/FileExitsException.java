package com.kyawgyi.api.exceptions;

public class FileExitsException extends RuntimeException{

    public FileExitsException(String message) {
        super(message);
    }
}
