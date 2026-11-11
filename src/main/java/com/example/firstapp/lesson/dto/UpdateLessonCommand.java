package com.example.firstapp.lesson.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateLessonCommand(

        @Positive(message = "NON_POSITIVE_VALUE")
        Long teacherId,
        @Positive(message = "NON_POSITIVE_VALUE")
        Long studentId,
        @Future(message = "DATE_NOT_IN_FUTURE")
        LocalDateTime dateTime
) {
}
