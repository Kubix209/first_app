package com.example.firstapp.common.exception.dto;

public record ViolationInfo(
        String field,
        String message
) {
}
