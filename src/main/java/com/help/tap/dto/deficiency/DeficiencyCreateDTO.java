package com.help.tap.dto.deficiency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeficiencyCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O tipo de deficiência é obrigatório")
        @Size(max = 150, message = "O tipo deve ter no máximo 150 caracteres")
        String type,

        // Detalhes adicionais relevantes para o APH — opcional
        @Size(max = 3000, message = "A descrição deve ter no máximo 3000 caracteres")
        String description
) {}