package com.example.firstapp.lesson.dto;

import com.example.firstapp.lesson.model.Lesson;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record LessonDto(

        Long id,
        Long teacherId,
        Long studentId,
        LocalDateTime dateTime

) {

    public static LessonDto fromEntity(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .teacherId(lesson.getTeacher().getId())
                .studentId(lesson.getStudent().getId())
                .dateTime(lesson.getDateTime())
                .build();
    }

}
