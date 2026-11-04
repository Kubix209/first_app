package com.example.firstapp.lesson.dto;

import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.model.Teacher;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateLessonCommand(

        Long teacherId,
        Long studentId,
        LocalDateTime dateTime

) {

    public static Lesson toEntity(CreateLessonCommand command, Teacher teacher, Student student) {
        return Lesson.builder()
                .teacher(teacher)
                .student(student)
                .dateTime(command.dateTime)
                .build();
    }

}
