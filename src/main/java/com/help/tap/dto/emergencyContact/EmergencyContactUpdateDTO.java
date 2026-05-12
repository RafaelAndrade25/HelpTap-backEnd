package com.help.tap.dto.emergencyContact;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmergencyContactUpdateDTO(

        @Pattern(
                regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$",
                message = "O telefone deve estar no formato (XX) XXXXX-XXXX"
        )
        String phone,

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String phoneOwner,

        @Size(max = 60, message = "O grau de parentesco deve ter no máximo 60 caracteres")
        String degreeOfKinship
) {}