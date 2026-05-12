package com.help.tap.service;

import com.help.tap.dto.wearable.WearableCreateDTO;
import com.help.tap.dto.wearable.WearableResponseDTO;
import com.help.tap.dto.wearable.WearableUpdateDTO;
import com.help.tap.model.User;
import com.help.tap.model.Wearable;
import com.help.tap.repository.UserRepository;
import com.help.tap.repository.WearableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public WearableResponseDTO createWearable(WearableCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found for ID: " + dto.userId()));

        Wearable wearable = Wearable.builder()
                .user(user)
                .wearableName(dto.wearableName())
                .status(true)                      // ativa por padrão
                .accessUrl(generateUniqueUUID())   // UUID gerado e validado no servidor
                .bindingDate(LocalDate.now())      // data de vínculo automática
                .build();

        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable));
    }

    @Transactional(readOnly = true)
    public WearableResponseDTO getWearableById(Integer id) {
        Wearable wearable = wearableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bracelet not found for ID: " + id));

        return WearableResponseDTO.fromEntity(wearable);
    }

    @Transactional(readOnly = true)
    public List<WearableResponseDTO> getWearablesByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found for ID: " + userId);
        }

        return wearableRepository.findByUser_Id(userId)
                .stream()
                .map(WearableResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public WearableResponseDTO updateWearable(Integer id, WearableUpdateDTO dto) {
        Wearable wearable = wearableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bracelet not found for ID: " + id));

        // accessUrl e bindingDate são imutáveis — nunca tocados aqui
        if (dto.wearableName() != null) wearable.setWearableName(dto.wearableName());
        if (dto.status()       != null) wearable.setStatus(dto.status());

        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable));
    }

    /**
     * Ativa ou desativa uma pulseira sem alterar nenhum outro dado.
     * Endpoint dedicado: PATCH /api/wearables/{id}/status
     */
    @Transactional
    public WearableResponseDTO toggleStatus(Integer id) {
        Wearable wearable = wearableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bracelet not found for ID: " + id));

        wearable.setStatus(!wearable.getStatus());

        return WearableResponseDTO.fromEntity(wearableRepository.save(wearable));
    }

    @Transactional
    public void deleteWearable(Integer id) {
        if (!wearableRepository.existsById(id)) {
            throw new EntityNotFoundException("Bracelet not found for ID: " + id);
        }
        wearableRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Gera um UUID v4 garantidamente único no banco.
     * A colisão de UUIDs é astronomicamente improvável, mas a verificação
     * existe como camada extra de segurança para um dado tão crítico.
     */
    private UUID generateUniqueUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (wearableRepository.existsByAccessUrl(uuid));
        return uuid;
    }
}
