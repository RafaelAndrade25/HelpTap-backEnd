package com.help.tap.dto.allergies;

import com.help.tap.model.Allergies;
import com.help.tap.model.RiskRating;

public record AllergyResponseDTO(
        Integer allergyId,
        Integer userId,
        String allergenic,
        RiskRating riskRating
) {
    public static AllergyResponseDTO fromEntity(Allergies allergy) {
        return new AllergyResponseDTO(
                allergy.getIlnessId(),
                allergy.getUser().getId(),
                allergy.getAllergenic(),
                allergy.getRiskRating()
        );
    }
}