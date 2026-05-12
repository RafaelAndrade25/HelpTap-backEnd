package com.help.tap.dto.wearable;

import jakarta.validation.constraints.Size;

public record WearableUpdateDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String wearableName,

        // accessUrl e bindingDate são imutáveis após criação
        // Para ativar/desativar use PATCH /wearable/{id}/status

        Boolean status
) {}