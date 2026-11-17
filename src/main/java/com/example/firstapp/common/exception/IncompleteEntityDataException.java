package com.example.firstapp.common.exception;

public class IncompleteEntityDataException extends RuntimeException {
    public IncompleteEntityDataException(String message) {
        super(message);
    }
}
