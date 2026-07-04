package ru.practicum.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO для унифицированного ответа об ошибках.
 * Содержит код ошибки, сообщение и детали валидации.
 */
@Data
@Builder
public class ErrorResponse {
    private int errorCode;
    private String message;
    private String error;
    private List<String> details;
    private String stackTrace;
}
