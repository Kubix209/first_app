package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import lombok.Builder;

import java.util.Set;

@Builder
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
