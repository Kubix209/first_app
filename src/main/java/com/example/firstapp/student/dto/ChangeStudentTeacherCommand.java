package com.example.firstapp.student.dto;

import lombok.Builder;

@Builder
public record ChangeStudentTeacherCommand(

        Long teacherId

) {
}
