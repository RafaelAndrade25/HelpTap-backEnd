package com.help.tap.dto.wearable;

import com.help.tap.model.Wearable;

import java.time.LocalDate;
import java.util.UUID;

public record WearableResponseDTO(
        Integer id,
        Integer userId,
        String wearableName,
        Boolean status,
        UUID accessUrl,
        LocalDate bindingDate
) {
    public static WearableResponseDTO fromEntity(Wearable wearable) {
        return new WearableResponseDTO(
                wearable.getId(),
                wearable.getUser().getId(),
                wearable.getWearableName(),
                wearable.getStatus(),
                wearable.getAccessUrl(),
                wearable.getBindingDate()
        );
    }
}
