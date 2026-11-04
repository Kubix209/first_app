package com.example.firstapp.student.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import lombok.Builder;

@Builder
public record UpdateStudentCommand(

        String firstName,
        String lastName,
        Language language

) {

    public static Student updateEntity(UpdateStudentCommand command, Student student) {

        if (command.firstName() != null) {
            student.setFirstName(command.firstName());
        }
        if (command.lastName() != null) {
            student.setLastName(command.lastName());
        }
        if (command.language() != null) {
            student.setLanguage(command.language());
        }

        return student;
    }

}
