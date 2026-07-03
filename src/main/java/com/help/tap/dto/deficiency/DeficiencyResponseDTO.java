package com.help.tap.dto.deficiency;

import com.help.tap.model.Deficiency;

public record DeficiencyResponseDTO(
        Integer id,
        Integer userId,
        String type,
        String description
) {
    public static DeficiencyResponseDTO fromEntity(Deficiency deficiency) {
        return new DeficiencyResponseDTO(
                deficiency.getId(),
                deficiency.getUser().getId(),
                deficiency.getType(),
                deficiency.getDescription()
        );
    }
}
