package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;

import java.util.Set;

public record CreateTeacherCommand(

        String firstName,
        String lastName,
        Set<Language> languages

) {
}
