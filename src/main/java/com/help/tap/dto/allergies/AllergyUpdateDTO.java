package com.help.tap.dto.allergies;

import com.help.tap.model.RiskRating;
import jakarta.validation.constraints.Size;

public record AllergyUpdateDTO(

        @Size(max = 150, message = "O alergênico deve ter no máximo 150 caracteres")
        String allergenic,

        RiskRating riskRating
) {}