package com.help.tap.dto.nfc;


import com.help.tap.model.AccessLog;
import com.help.tap.model.AccessLog.AccessLevel;
import com.help.tap.model.UserRole;

import java.time.LocalDateTime;

public record AccessLogResponseDTO(
        Integer id,
        Integer wearableId,
        String wearableName,
        UserRole readerRole,
        String readerEmail,
        LocalDateTime accessedAt,
        Double latitude,
        Double longitude,
        AccessLevel accessLevel
) {
    public static AccessLogResponseDTO fromEntity(AccessLog log) {
        return new AccessLogResponseDTO(
                log.getId(),
                log.getWearable().getId(),
                log.getWearable().getWearableName(),
                log.getReaderRole(),
                log.getReaderEmail(),
                log.getAccessedAt(),
                log.getLatitude(),
                log.getLongitude(),
                log.getAccessLevel()
        );
    }
}