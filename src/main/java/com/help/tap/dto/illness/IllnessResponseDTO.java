package com.help.tap.dto.illness;

import com.help.tap.model.RiskRating;


public record IllnessResponseDTO(
        Integer illnessId,
        Integer userId,
        String illnessName,
        Boolean isSensitive,
        String notes,
        RiskRating riskRating
) {}