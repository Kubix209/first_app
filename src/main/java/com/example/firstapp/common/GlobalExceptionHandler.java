package com.example.firstapp.common;

import com.example.firstapp.common.exception.*;
import com.example.firstapp.common.exception.dto.ExceptionDto;
import com.example.firstapp.common.exception.dto.ValidationExceptionDto;
import com.example.firstapp.common.exception.dto.ViolationInfo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionDto handleValidationException(MethodArgumentNotValidException exception) {
        List<ViolationInfo> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ViolationInfo(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return new ValidationExceptionDto(violations);
    }

    @ExceptionHandler(LanguageMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleLanguageMismatchException(LanguageMismatchException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(LessonDateUnavailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleLessonDateUnavailableException(LessonDateUnavailableException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(LessonInPastException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleLessonInPastException(LessonInPastException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(TeacherNotAssignedToStudentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleTeacherNotAssignedToStudentException(TeacherNotAssignedToStudentException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(IncompleteEntityDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleIncompleteEntityDataException(IncompleteEntityDataException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(EmptyUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleEmptyUpdateException(EmptyUpdateException exception) {
        return new ExceptionDto(exception.getMessage());
    }

}
