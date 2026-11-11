package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record UpdateTeacherCommand(
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String firstName,
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String lastName,
        @Size(min = 1, message = "EMPTY_VALUE")
        Set<@NotNull(message = "NULL_VALUE") Language> languages
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
