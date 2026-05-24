package com.help.tap.dto.disorder;

import jakarta.validation.constraints.Size;

public record DisorderUpdateDTO(

        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String disorderName,

        @Size(max = 60, message = "O grau deve ter no máximo 60 caracteres")
        String disorderDegree,

        @Size(max = 3000, message = "A descrição deve ter no máximo 3000 caracteres")
        String description
) {}