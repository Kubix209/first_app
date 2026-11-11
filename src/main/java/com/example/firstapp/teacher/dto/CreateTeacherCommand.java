package com.example.firstapp.teacher.dto;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateTeacherCommand(
        @NotNull(message = "NULL_VALUE")
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String firstName,
        @NotNull(message = "NULL_VALUE")
        @Pattern(regexp = "[A-Z][a-z]{0,18}", message = "PATTERN_MISMATCH")
        String lastName,
        @NotEmpty(message = "EMPTY_VALUE")
        Set<@NotNull(message = "NULL_VALUE") Language> languages
) {
    public static Teacher toEntity(CreateTeacherCommand command) {
        return Teacher.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .languages(command.languages())
                .build();
    }
}

//zadanie z * polega na refaktor update lesson > niech mi chat podpowie ale nakieruj go zebys uzyl json nullable
// i method reference i metody pomocnicze zrobic bezposrednio w encji (np. change first name) i jedna pomocnicza replace ktora bedzied kozystac z tamtych
// bedzie sie to zachowywac jak optional wiec bede korzystal z  ifPresent na kazdym polu z commanda bede robil ifPresent i method reference
// ewentualnei zrobic nowego commanda specjalnie pod to ktory bedize rekordem i tam bedzie ten json nullable i wtedy powinno byc bez ifow


//zapamietaj ze dodanei walidacji upraszcza walidacje w serwisie

//dopisz wszedzie odowiednie statusy

//next lekcja global exception handler

//testy integracyjne liznac troche teorii

//z loga wyciagnac all ifno i za pomoca global exception handler spowodowac ze odpowiedz z bledem bad request w postmanie bedzie przekazywala informacje co sie stalo