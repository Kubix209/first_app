package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;

import java.util.Set;

public record UpdateTeacherCommand(

        String firstName,
        String lastName,
        Set<Language> languages

) {

    public static Teacher updateEntity(UpdateTeacherCommand command, Teacher teacher) {

        if (command.firstName() != null) {
            teacher.setFirstName(command.firstName());
        }
        if (command.lastName() != null) {
            teacher.setLastName(command.lastName());
        }
        if (command.languages() != null) {
            teacher.setLanguages(command.languages());
        }

        return teacher;
    }

}
