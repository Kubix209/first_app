package com.example.firstapp.common.exception;

public class LessonInPastException extends RuntimeException {
    public LessonInPastException(String message) {
        super(message);
    }
}
