package com.help.tap.dto.profile;

import com.help.tap.dto.allergies.AllergyResponseDTO;
import com.help.tap.dto.deficiency.DeficiencyResponseDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactResponseDTO;

import java.util.List;

public record PublicProfileDTO(

        Integer userId,
        String fullName,
        String bloodType,
        Boolean organDonor,

        // Alergias críticas — apenas HIGH e CRITICAL (ver AllergyRepository)
        List<AllergyResponseDTO> criticalAllergies,

        // Deficiências — sempre públicas
        List<DeficiencyResponseDTO> deficiencies,

        // Contatos de emergência
        List<EmergencyContactResponseDTO> emergencyContacts
) {}
