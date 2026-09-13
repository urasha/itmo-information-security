package ru.itmo.securitylab.dto;

import java.util.Map;

public record ErrorResponse(String code, String message, Map<String, String> fieldErrors) {
    public ErrorResponse {
        fieldErrors = Map.copyOf(fieldErrors);
    }

    public ErrorResponse(String code, String message) {
        this(code, message, Map.of());
    }
}
