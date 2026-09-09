package com.help.tap.service;

import com.help.tap.dto.wearable.WearableCreateDTO;
import com.help.tap.dto.wearable.WearableResponseDTO;
import com.help.tap.dto.wearable.WearableUpdateDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.exception.WearableLimitExceededException;
import com.help.tap.model.User;
import com.help.tap.model.Wearable;
import com.help.tap.repository.UserRepository;
import com.help.tap.repository.WearableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WearableService {

    private final WearableRepository wearableRepository;
    private final UserRepository userRepository;

    @Value("${helptap.wearable.max-per-user:5}")
    private int maxWearablesPerUser;

    // Ajuste da URL base para apontar para o app web real
    @Value("${helptap.wearable.base-url:https://helptap-web.vercel.app/pulseira/}")
    private String baseUrl;

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Transactional
    public WearableResponseDTO createWearable(WearableCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));
        enforceWearableLimit(dto.userId());

        Wearable wearable = Wearable.builder()
                .user(user)
                .wearableName(dto.wearableName())
                .status(true)
                .accessUrl(generateUniqueUUID()) // REGRA 2: UUID único garantido
                .bindingDate(LocalDate.now())
                .build();

        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable), baseUrl);
    }

    @Transactional(readOnly = true)
    public WearableResponseDTO getWearableById(Integer id) {
        return WearableResponseDTO.fromEntity(findOrThrow(id), baseUrl);
    }

    @Transactional(readOnly = true)
    public List<WearableResponseDTO> getWearablesByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return wearableRepository.findByUser_IdAndDeletedFalse(userId)
                .stream()
                .map(wearable -> WearableResponseDTO.fromEntity(wearable, baseUrl))
                .toList();
    }

    @Transactional
    public WearableResponseDTO updateWearable(Integer id, WearableUpdateDTO dto) {
        Wearable wearable = findOrThrow(id);

        // REGRA 3: accessUrl e bindingDate são imutáveis — nunca expostos no DTO de
        // update
        if (dto.wearableName() != null)
            wearable.setWearableName(dto.wearableName());
        if (dto.status() != null)
            wearable.setStatus(dto.status());

        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable), baseUrl);
    }

    @Transactional
    public WearableResponseDTO toggleStatus(Integer id) {
        Wearable wearable = findOrThrow(id);
        wearable.setStatus(!wearable.getStatus());
        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable), baseUrl);
    }

    @Transactional
    public void deleteWearable(Integer id) {
        Wearable wearable = findOrThrow(id);

        // REGRA 4: não permite excluir pulseira ativa — deve ser desativada antes
        if (Boolean.TRUE.equals(wearable.getStatus())) {
            throw new BusinessRuleException(
                    "Não é possível excluir uma pulseira ativa. " +
                            "Desative-a primeiro via PATCH /api/wearables/" + id + "/status.");
        }

        wearable.setDeleted(true);
        wearableRepository.save(wearable);
    }

    private Wearable findOrThrow(Integer id) {
        return wearableRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pulseira não encontrada com ID: " + id));
    }

    private void enforceWearableLimit(Integer userId) {
        int count = wearableRepository.countByUser_IdAndDeletedFalse(userId);
        if (count >= maxWearablesPerUser) {
            throw new WearableLimitExceededException(userId, maxWearablesPerUser);
        }
    }

    private UUID generateUniqueUUID() {
        UUID uuid;
        int attempts = 0;
        do {
            if (attempts++ > 10) {
                throw new BusinessRuleException(
                        "Não foi possível gerar um UUID único após múltiplas tentativas. " +
                                "Contate o suporte.");
            }
            uuid = UUID.randomUUID();
        } while (wearableRepository.existsByAccessUrl(uuid));
        return uuid;
    }
}