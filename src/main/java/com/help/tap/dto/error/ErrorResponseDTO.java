package com.help.tap.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        List<String> details         // usado para erros de validação (@Valid)
) {
    // Construtor simplificado para erros sem lista de detalhes
    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(status, error, message, path, LocalDateTime.now(), null);
    }

    // Construtor para erros de validação com múltiplos campos inválidos
    public static ErrorResponseDTO ofValidation(String path, List<String> details) {
        return new ErrorResponseDTO(
                422,
                "Unprocessable Entity",
                "Um ou mais campos falharam na validação.",
                path,
                LocalDateTime.now(),
                details
        );
    }
}