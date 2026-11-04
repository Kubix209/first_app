package com.example.firstapp.common.exception;

public class TeacherNotAssignedToStudentException extends RuntimeException {
    public TeacherNotAssignedToStudentException(String message) {
        super(message);
    }
}
