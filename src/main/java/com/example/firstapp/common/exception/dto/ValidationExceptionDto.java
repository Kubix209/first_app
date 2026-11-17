package com.example.firstapp.common.exception.dto;

import java.util.List;

public record ValidationExceptionDto(
        List<ViolationInfo> violations
){
}