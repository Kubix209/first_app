package com.example.firstapp.lesson.dto;

import java.time.LocalDateTime;

public record LessonDto(
        Long id,
        LocalDateTime dateTime
) {
}
