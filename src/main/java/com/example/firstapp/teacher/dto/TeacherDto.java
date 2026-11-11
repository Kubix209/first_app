package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import lombok.Builder;

import java.util.Set;

@Builder
public record TeacherDto(
        Long id,
        String firstName,
        String lastName,
        Set<Language> languages
) {
    public static TeacherDto fromEntity(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .languages(teacher.getLanguages())
                .build();
    }
}
