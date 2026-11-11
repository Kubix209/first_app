package com.example.firstapp.student.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import lombok.Builder;

@Builder
public record StudentDto(
        Long id,
        String firstName,
        String lastName,
        Language language
) {
    public static StudentDto fromEntity(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .language(student.getLanguage())
                .build();
    }
}
