package com.example.firstapp.student.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.model.Teacher;
import lombok.Builder;

@Builder
public record CreateStudentCommand(

        String firstName,
        String lastName,
        Language language,
        Long teacherId

) {

    public static Student toEntity(CreateStudentCommand command, Teacher teacher) {
        return Student.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .language(command.language())
                .teacher(teacher)
                .build();
    }

}
