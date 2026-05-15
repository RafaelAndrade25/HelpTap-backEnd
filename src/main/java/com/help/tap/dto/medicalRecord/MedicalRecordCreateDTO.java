package com.help.tap.dto.medicalRecord;

import jakarta.validation.constraints.*;

public record MedicalRecordCreateDTO(

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer userId,

        @NotBlank(message = "O tipo sanguíneo é obrigatório")
        @Pattern(
                regexp = "^(A|B|AB|O)[+-]$",
                message = "Tipo sanguíneo inválido. Use: A+, A-, B+, B-, AB+, AB-, O+ ou O-"
        )
        String bloodType,

        @NotNull(message = "A altura é obrigatória")
        @Min(value = 50,  message = "Altura mínima permitida: 50 cm")
        @Max(value = 300, message = "Altura máxima permitida: 300 cm")
        Integer height,

        @NotNull(message = "O peso é obrigatório")
        @DecimalMin(value = "1.0",   message = "Peso mínimo permitido: 1,0 kg")
        @DecimalMax(value = "700.0", message = "Peso máximo permitido: 700,0 kg")
        Double weight,

        @NotBlank(message = "A etnia é obrigatória")
        @Size(max = 60, message = "A etnia deve ter no máximo 60 caracteres")
        String ethnicity,

        @NotNull(message = "Informe se o paciente é doador de órgãos")
        Boolean organDonor,

        // Campo livre opcional — observações clínicas gerais
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description
) {}
