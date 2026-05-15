package com.help.tap.dto.medicalRecord;

import com.help.tap.model.MedicalRecord;

public record MedicalRecordResponseDTO(
        Integer medicalRecordId,
        Integer userId,
        String bloodType,
        Integer height,
        Double weight,
        String ethnicity,
        Boolean organDonor,
        String description
) {
    public static MedicalRecordResponseDTO fromEntity(MedicalRecord record) {
        return new MedicalRecordResponseDTO(
                record.getMedicalRecordId(),
                record.getUser().getId(),
                record.getBloodType(),
                record.getHeight(),
                record.getWeight(),
                record.getEthnicity(),
                record.getOrganDonor(),
                record.getDescription()
        );
    }
}