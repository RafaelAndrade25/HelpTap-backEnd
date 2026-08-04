package com.help.tap.dto.wearable;

import com.help.tap.model.Wearable;

import java.time.LocalDate;

public record WearableResponseDTO(
        Integer id,
        Integer userId,
        String wearableName,
        Boolean status,
        String accessUrl,
        LocalDate bindingDate
) {
    public static WearableResponseDTO fromEntity(Wearable wearable, String baseUrl) {
        return new WearableResponseDTO(
                wearable.getId(),
                wearable.getUser().getId(),
                wearable.getWearableName(),
                wearable.getStatus(),
                baseUrl + wearable.getAccessUrl().toString(),
                wearable.getBindingDate()
        );
    }
}
