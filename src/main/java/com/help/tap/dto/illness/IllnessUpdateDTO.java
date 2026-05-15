package com.help.tap.dto.illness;

import com.help.tap.model.RiskRating;
import jakarta.validation.constraints.Size;

public record IllnessUpdateDTO(

        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String illnessName,

        /**
         * Alterar isSensitive de false → true re-criptografa os dados existentes.
         * Alterar de true → false descriptografa e salva em texto claro.
         * O service trata ambas as direções.
         */
        Boolean isSensitive,

        @Size(max = 2000, message = "As notas devem ter no máximo 2000 caracteres")
        String notes,

        RiskRating riskRating
) {}