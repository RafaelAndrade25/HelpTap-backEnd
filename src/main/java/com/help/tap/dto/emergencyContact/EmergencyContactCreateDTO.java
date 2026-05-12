package com.help.tap.dto.emergencyContact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmergencyContactCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(
                regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$",
                message = "O telefone deve estar no formato (XX) XXXXX-XXXX"
        )
        String phone,

        @NotBlank(message = "O nome do responsável pelo telefone é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String phoneOwner,

        @NotBlank(message = "O grau de parentesco é obrigatório")
        @Size(max = 60, message = "O grau de parentesco deve ter no máximo 60 caracteres")
        String degreeOfKinship
) {}