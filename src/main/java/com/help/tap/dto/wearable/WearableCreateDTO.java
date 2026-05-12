package com.help.tap.dto.wearable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WearableCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O nome da pulseira é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String wearableName

        // accessUrl é gerado pelo servidor — nunca recebido do cliente
        // bindingDate é definida automaticamente na criação
        // status inicia como true (ativo) por padrão
) {}
