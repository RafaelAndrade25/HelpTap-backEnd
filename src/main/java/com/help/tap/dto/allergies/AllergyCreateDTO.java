package com.help.tap.dto.allergies;

import com.help.tap.model.RiskRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AllergyCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O agente alergênico é obrigatório")
        @Size(max = 150, message = "O alergênico deve ter no máximo 150 caracteres")
        String allergenic,

        @NotNull(message = "A classificação de risco da alergia é obrigatória")
        RiskRating riskRating
) {}