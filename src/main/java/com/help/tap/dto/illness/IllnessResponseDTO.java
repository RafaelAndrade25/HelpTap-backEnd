package com.help.tap.dto.illness;

import com.help.tap.model.RiskRating;

/**
 * O mapeamento fromEntity() foi movido para o IllnessService.
 * Motivo: getDecryptedName() e getDecryptedNotes() da entidade lançam Exception
 * e dependem do EncryptionUtil, que só está disponível no contexto Spring (service).
 * Um método estático em record não consegue lidar com isso de forma limpa.
 */
public record IllnessResponseDTO(
        Integer illnessId,
        Integer userId,
        String illnessName,
        Boolean isSensitive,
        String notes,
        RiskRating riskRating
) {}