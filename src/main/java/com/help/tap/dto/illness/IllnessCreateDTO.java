package com.help.tap.dto.illness;

import com.help.tap.model.RiskRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IllnessCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O nome da doença é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String illnessName,

        /**
         * true  → illnessName e notes serão criptografados no banco
         *         e só exibidos para leitores com credencial profissional validada.
         * false → dados visíveis para qualquer leitura autenticada.
         */
        @NotNull(message = "Informe se o dado é sensível (true/false)")
        Boolean isSensitive,

        @Size(max = 2000, message = "As notas devem ter no máximo 2000 caracteres")
        String notes,

        @NotNull(message = "O nível de risco é obrigatório")
        RiskRating riskRating
) {}