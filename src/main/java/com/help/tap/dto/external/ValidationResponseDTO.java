package com.help.tap.dto.external;

public record ValidationResponseDTO(
        boolean valid,
        String credential,
        String uf,
        String type,
        String name,
        String specialty,
        String status,
        String message
) {}
