package br.com.andredevel.user.service.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    Map<String, Object> details,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message, Map<String, Object> details) {
        this(code, message, details, LocalDateTime.now());
    }

    public ErrorResponse(String code, String message) {
        this(code, message, null, LocalDateTime.now());
    }
}