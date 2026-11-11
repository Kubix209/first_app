package com.example.firstapp.student.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ChangeStudentTeacherCommand(
        @NotNull(message = "NULL_VALUE")
        @Positive(message = "NON_POSITIVE_VALUE")
        Long teacherId
) {
}
