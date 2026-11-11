package com.example.firstapp.student.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateStudentCommand(
        @NotNull(message = "NULL_VALUE")
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String firstName,
        @NotNull(message = "NULL_VALUE")
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String lastName,
        @NotNull(message = "NULL_VALUE")
        Language language,
        @NotNull(message = "NULL_VALUE")
        @Positive(message = "NON_POSITIVE_VALUE")
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
