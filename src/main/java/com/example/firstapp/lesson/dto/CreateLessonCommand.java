package com.example.firstapp.lesson.dto;

import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateLessonCommand(
        @NotNull(message = "NULL_VALUE")
        @Positive(message = "NON_POSITIVE_VALUE")
        Long teacherId,
        @NotNull(message = "NULL_VALUE")
        @Positive(message = "NON_POSITIVE_VALUE")
        Long studentId,
        @NotNull(message = "NULL_VALUE")
        @Future(message = "DATE_NOT_IN_FUTURE")
        LocalDateTime dateTime
) {
    public static Lesson toEntity(CreateLessonCommand command, Teacher teacher, Student student) {
        return Lesson.builder()
                .teacher(teacher)
                .student(student)
                .dateTime(command.dateTime())
                .build();
    }
}
