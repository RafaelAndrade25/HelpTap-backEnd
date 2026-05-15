package com.help.tap.dto.medicalRecord;

import jakarta.validation.constraints.*;

public record MedicalRecordUpdateDTO(

        @Pattern(
                regexp = "^(A|B|AB|O)[+-]$",
                message = "Tipo sanguíneo inválido. Use: A+, A-, B+, B-, AB+, AB-, O+ ou O-"
        )
        String bloodType,

        @Min(value = 50,  message = "Altura mínima permitida: 50 cm")
        @Max(value = 300, message = "Altura máxima permitida: 300 cm")
        Integer height,

        @DecimalMin(value = "1.0",   message = "Peso mínimo permitido: 1,0 kg")
        @DecimalMax(value = "700.0", message = "Peso máximo permitido: 700,0 kg")
        Double weight,

        @Size(max = 60, message = "A etnia deve ter no máximo 60 caracteres")
        String ethnicity,

        Boolean organDonor,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description
) {}
