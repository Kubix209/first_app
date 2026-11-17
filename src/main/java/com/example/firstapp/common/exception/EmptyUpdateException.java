package com.example.firstapp.common.exception;

public class EmptyUpdateException extends RuntimeException {
    public EmptyUpdateException(String message) {
        super(message);
    }
}
