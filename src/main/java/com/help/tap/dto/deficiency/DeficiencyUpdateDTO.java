package com.help.tap.dto.deficiency;

import jakarta.validation.constraints.Size;

public record DeficiencyUpdateDTO(

        @Size(max = 150, message = "O tipo deve ter no máximo 150 caracteres")
        String type,

        @Size(max = 3000, message = "A descrição deve ter no máximo 3000 caracteres")
        String description
) {}
