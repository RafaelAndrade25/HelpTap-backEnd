package com.help.tap.dto.profile;

import com.help.tap.dto.allergies.AllergyResponseDTO;
import com.help.tap.dto.deficiency.DeficiencyResponseDTO;
import com.help.tap.dto.disorder.DisorderResponseDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactResponseDTO;
import com.help.tap.dto.illness.IllnessResponseDTO;

import java.util.List;

public record ProfessionalProfileDTO(

        // Identificação
        Integer userId,
        String fullName,

        // Ficha médica completa
        MedicalSummaryDTO medicalRecord,

        List<IllnessResponseDTO> illnesses,

        List<DisorderResponseDTO> disorders,

        List<AllergyResponseDTO> allergies,

        List<DeficiencyResponseDTO> deficiencies,

        List<EmergencyContactResponseDTO> emergencyContacts,

        String viewerRole
) {}
