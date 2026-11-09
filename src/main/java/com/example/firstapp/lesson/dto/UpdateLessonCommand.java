package com.example.firstapp.lesson.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateLessonCommand(
        Long teacherId,
        Long studentId,
        LocalDateTime dateTime
) {
}
