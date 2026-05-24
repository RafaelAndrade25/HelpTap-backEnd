package com.help.tap.dto.disorder;

public record DisorderResponseDTO(
        Integer disorderId,
        Integer userId,
        String disorderName,
        String disorderDegree,
        String description,
        boolean sensitive
) {}