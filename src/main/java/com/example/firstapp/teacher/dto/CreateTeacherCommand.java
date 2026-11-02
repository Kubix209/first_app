package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;

import java.util.Set;

public record CreateTeacherCommand(

        String firstName,
        String lastName,
        Set<Language> languages

) {

    public static Teacher toEntity(CreateTeacherCommand command) {
        return Teacher.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .languages(command.languages())
                .build();
    }

}
