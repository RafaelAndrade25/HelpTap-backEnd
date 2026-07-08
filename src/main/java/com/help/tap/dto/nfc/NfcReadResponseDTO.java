package com.help.tap.dto.nfc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.help.tap.dto.profile.ProfessionalProfileDTO;
import com.help.tap.dto.profile.PublicProfileDTO;
import com.help.tap.model.AccessLog.AccessLevel;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NfcReadResponseDTO(

        Integer accessLogId,
        AccessLevel accessLevel,
        LocalDateTime readAt,
        String accessUrl,
        PublicProfileDTO publicProfile,
        ProfessionalProfileDTO professionalProfile) {

    public static NfcReadResponseDTO ofPublic(Integer logId,
                                              String accessUrl,
                                              PublicProfileDTO profile) {
        return new NfcReadResponseDTO(
                logId,
                AccessLevel.PUBLIC,
                LocalDateTime.now(),
                accessUrl,
                profile,
                null
        );
    }

    public static NfcReadResponseDTO ofProfessional(Integer logId,
                                                    String accessUrl,
                                                    ProfessionalProfileDTO profile) {
        return new NfcReadResponseDTO(
                logId,
                AccessLevel.PROFESSIONAL,
                LocalDateTime.now(),
                accessUrl,
                null,
                profile
        );
    }
}