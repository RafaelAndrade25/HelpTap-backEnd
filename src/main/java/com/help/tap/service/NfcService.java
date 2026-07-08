package com.help.tap.service;

import com.help.tap.dto.nfc.AccessLogResponseDTO;
import com.help.tap.dto.nfc.NfcReadRequestDTO;
import com.help.tap.dto.nfc.NfcReadResponseDTO;
import com.help.tap.dto.profile.ProfessionalProfileDTO;
import com.help.tap.dto.profile.PublicProfileDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.model.AccessLog;
import com.help.tap.model.AccessLog.AccessLevel;
import com.help.tap.model.UserRole;
import com.help.tap.model.Wearable;
import com.help.tap.repository.AccessLogRepository;
import com.help.tap.repository.WearableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NfcService {

    private final WearableRepository wearableRepository;
    private final AccessLogRepository accessLogRepository;
    private final ProfileService      profileService;

    /**
     * URL base da plataforma web — configurável sem recompilar.
     * application.properties: helptap.base-url=https://app.helptap.com.br
     */
    @Value("${helptap.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public NfcReadResponseDTO readPublic(UUID uuid, NfcReadRequestDTO request) {
        Wearable wearable = resolveAndValidate(uuid);
        Integer userId    = wearable.getUser().getId();

        PublicProfileDTO profile = profileService.buildPublicProfile(userId);

        AccessLog log = registerLog(
                wearable, null, null,
                request, AccessLevel.PUBLIC
        );

        String accessUrl = buildAccessUrl(uuid, false);

        return NfcReadResponseDTO.ofPublic(log.getId(), accessUrl, profile);
    }

    // -------------------------------------------------------------------------
    // LEITURA NFC PROFISSIONAL — requer autenticação
    // -------------------------------------------------------------------------

    @Transactional
    public NfcReadResponseDTO readProfessional(UUID uuid,
                                               NfcReadRequestDTO request,
                                               Authentication authentication) {
        Wearable wearable = resolveAndValidate(uuid);
        Integer userId    = wearable.getUser().getId();

        UserRole role        = extractRole(authentication);
        String   readerEmail = authentication.getName();

        ProfessionalProfileDTO profile =
                profileService.buildProfessionalProfile(userId, authentication);

        AccessLog log = registerLog(
                wearable, role, readerEmail,
                request, AccessLevel.PROFESSIONAL
        );

        String accessUrl = buildAccessUrl(uuid, true);

        return NfcReadResponseDTO.ofProfessional(log.getId(), accessUrl, profile);
    }

    // -------------------------------------------------------------------------
    // HISTÓRICO DE ACESSOS — visível para o dono da pulseira e Admin
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AccessLogResponseDTO> getLogsByWearable(Integer wearableId) {
        if (!wearableRepository.existsById(wearableId)) {
            throw new EntityNotFoundException("Pulseira não encontrada com ID: " + wearableId);
        }
        return accessLogRepository
                .findByWearable_IdOrderByAccessedAtDesc(wearableId)
                .stream()
                .map(AccessLogResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccessLogResponseDTO> getLogsByUser(Integer userId) {
        return accessLogRepository
                .findByWearable_User_IdOrderByAccessedAtDesc(userId)
                .stream()
                .map(AccessLogResponseDTO::fromEntity)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------
    private Wearable resolveAndValidate(UUID uuid) {
        Wearable wearable = wearableRepository.findByAccessUrl(uuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pulseira não encontrada para o UUID informado. " +
                                "Verifique se a tag NFC está íntegra."));

        if (Boolean.FALSE.equals(wearable.getStatus())) {
            throw new BusinessRuleException(
                    "Esta pulseira está desativada e não pode ser lida. " +
                            "O usuário pode ter desvinculado este dispositivo.");
        }

        return wearable;
    }
    private AccessLog registerLog(Wearable wearable,
                                  UserRole role,
                                  String readerEmail,
                                  NfcReadRequestDTO request,
                                  AccessLevel level) {
        AccessLog log = AccessLog.builder()
                .wearable(wearable)
                .user(wearable.getUser())
                .readerRole(role)
                .readerEmail(readerEmail)
                .accessedAt(LocalDateTime.now())
                .latitude(request != null ? request.latitude()  : null)
                .longitude(request != null ? request.longitude() : null)
                .accessLevel(level)
                .build();

        return accessLogRepository.save(log);
    }

    private String buildAccessUrl(UUID uuid, boolean professional) {
        String base = baseUrl + "/p/" + uuid;
        return professional ? base + "?pro=true" : base;
    }

    private UserRole extractRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> {
                    try {
                        return UserRole.valueOf(a.getAuthority().replace("ROLE_", ""));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}
