package com.help.tap.dto.emergencyContact;

import com.help.tap.model.EmergencyContact;

public record EmergencyContactResponseDTO(
        Integer contactId,
        Integer userId,
        String phone,
        String phoneOwner,
        String degreeOfKinship
) {
    public static EmergencyContactResponseDTO fromEntity(EmergencyContact contact) {
        return new EmergencyContactResponseDTO(
                contact.getContactId(),
                contact.getUser().getId(),
                contact.getPhone(),
                contact.getPhoneOwner(),
                contact.getDegreeOfKinship()
        );
    }
}
