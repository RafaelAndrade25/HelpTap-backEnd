package com.help.tap.dto.profile;

import com.help.tap.model.MedicalRecord;

public record MedicalSummaryDTO(
        String bloodType,
        Integer height,
        Double weight,
        String ethnicity,
        Boolean organDonor,
        String description
) {
    public static MedicalSummaryDTO fromEntity(MedicalRecord record) {
        return new MedicalSummaryDTO(
                record.getBloodType(),
                record.getHeight(),
                record.getWeight(),
                record.getEthnicity(),
                record.getOrganDonor(),
                record.getDescription()
        );
    }
}
