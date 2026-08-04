package com.help.tap.service;

import com.help.tap.dto.illness.IllnessCreateDTO;
import com.help.tap.dto.illness.IllnessResponseDTO;
import com.help.tap.dto.illness.IllnessUpdateDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.infra.security.EncryptionUtil;
import com.help.tap.model.Illness;
import com.help.tap.model.RiskRating;
import com.help.tap.model.User;
import com.help.tap.repository.IllnessRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.help.tap.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IllnessService {

    private final IllnessRepository illnessRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public IllnessResponseDTO createIllness(IllnessCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        Illness illness = Illness.builder()
                .user(user)
                .isSensitive(dto.isSensitive())
                .riskRating(dto.riskRating())
                .build();

        // Injeta o utilitário de criptografia na entidade antes de chamar os setters,
        // pois a entidade decide internamente se criptografa ou não com base em isSensitive
        illness.setEncryptionUtil(encryptionUtil);

        try {
            illness.setName(dto.illnessName());
            illness.setNotes(dto.notes());
        } catch (Exception e) {
            log.error("Erro ao criptografar dados da doença para o usuário ID {}: {}",
                    dto.userId(), e.getMessage());
            throw new BusinessRuleException(
                    "Falha ao processar os dados sensíveis. Tente novamente.");
        }

        return toResponseDTO(illnessRepository.save(illness));
    }

    @Transactional(readOnly = true)
    public IllnessResponseDTO getIllnessById(Integer illnessId, Authentication authentication) {
        Illness illness = findOrThrow(illnessId);
        if (Boolean.TRUE.equals(illness.getIsSensitive()) && !canAccessSensitiveData(authentication, illness.getUser().getId())) {
            throw new BusinessRuleException("Acesso negado: dados sensíveis requerem permissão.");
        }
        return toResponseDTO(illness, authentication);
    }

    @Transactional(readOnly = true)
    public List<IllnessResponseDTO> getIllnessesByUserId(Integer userId, Authentication authentication) {
        ensureUserExists(userId);
        return illnessRepository.findByUser_Id(userId)
                .stream()
                .map(illness -> toResponseDTO(illness, authentication))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IllnessResponseDTO> getIllnessesByUserIdAndRisk(Integer userId,
                                                                RiskRating riskRating,
                                                                Authentication authentication) {
        ensureUserExists(userId);
        return illnessRepository.findByUser_IdAndRiskRating(userId, riskRating)
                .stream()
                .map(illness -> toResponseDTO(illness, authentication))
                .toList();
    }

    @Transactional
    public IllnessResponseDTO updateIllness(Integer illnessId, IllnessUpdateDTO dto) {
        Illness illness = findOrThrow(illnessId);

        // Injeta o utilitário antes de qualquer setter que possa criptografar
        illness.setEncryptionUtil(encryptionUtil);

        try {
            handleSensitivityChange(illness, dto);

            if (dto.illnessName() != null) illness.setName(dto.illnessName());
            if (dto.notes()       != null) illness.setNotes(dto.notes());
            if (dto.riskRating()  != null) illness.setRiskRating(dto.riskRating());

        } catch (Exception e) {
            log.error("Erro ao atualizar criptografia da doença ID {}: {}", illnessId, e.getMessage());
            throw new BusinessRuleException(
                    "Falha ao processar os dados sensíveis. Tente novamente.");
        }

        return toResponseDTO(illnessRepository.save(illness));
    }

    @Transactional
    public void deleteIllness(Integer illnessId) {
        if (!illnessRepository.existsById(illnessId)) {
            throw new EntityNotFoundException("Doença não encontrada com ID: " + illnessId);
        }
        illnessRepository.deleteById(illnessId);
    }

    /**
     * Converte entidade para DTO descriptografando os campos sensíveis se permitido.
     */
    private IllnessResponseDTO toResponseDTO(Illness illness, Authentication authentication) {
        illness.setEncryptionUtil(encryptionUtil);
        boolean canDecrypt = !Boolean.TRUE.equals(illness.getIsSensitive()) || 
                             canAccessSensitiveData(authentication, illness.getUser().getId());
                             
        try {
            return new IllnessResponseDTO(
                    illness.getIlnessId(),
                    illness.getUser().getId(),
                    canDecrypt ? illness.getDecryptedName() : "[DADOS SENSÍVEIS]",
                    illness.getIsSensitive(),
                    canDecrypt ? illness.getDecryptedNotes() : "[DADOS SENSÍVEIS]",
                    illness.getRiskRating()
            );
        } catch (Exception e) {
            log.error("Erro ao descriptografar doença ID {}: {}", illness.getIlnessId(), e.getMessage());
            throw new BusinessRuleException(
                    "Falha ao recuperar os dados sensíveis. Contate o suporte.");
        }
    }
    
    // Método sobrecarregado para uso interno onde a autenticação não importa (ex: create/update já validam dono)
    private IllnessResponseDTO toResponseDTO(Illness illness) {
        illness.setEncryptionUtil(encryptionUtil);
        try {
            return new IllnessResponseDTO(
                    illness.getIlnessId(),
                    illness.getUser().getId(),
                    illness.getDecryptedName(),
                    illness.getIsSensitive(),
                    illness.getDecryptedNotes(),
                    illness.getRiskRating()
            );
        } catch (Exception e) {
            log.error("Erro ao descriptografar doença ID {}: {}", illness.getIlnessId(), e.getMessage());
            throw new BusinessRuleException(
                    "Falha ao recuperar os dados sensíveis. Contate o suporte.");
        }
    }

    /**
     * Trata a mudança de isSensitive durante um update.
     *
     * false → true : re-criptografa os dados já existentes no banco.
     * true  → false: descriptografa os dados e salva em texto claro.
     * sem mudança  : mantém o estado atual; o comportamento dos setters cuida do resto.
     */
    private void handleSensitivityChange(Illness illness, IllnessUpdateDTO dto) throws Exception {
        if (dto.isSensitive() == null) return;

        boolean wasAlreadySensitive = Boolean.TRUE.equals(illness.getIsSensitive());
        boolean willBeSensitive     = dto.isSensitive();

        if (wasAlreadySensitive == willBeSensitive) return; // nenhuma mudança real

        if (!wasAlreadySensitive && willBeSensitive) {
            // Texto claro → criptografado: precisa criptografar o que já está no banco
            String currentName  = illness.getIllnessName();
            String currentNotes = illness.getNotes();
            illness.setIsSensitive(true);
            if (currentName  != null) illness.setName(currentName);
            if (currentNotes != null) illness.setNotes(currentNotes);

        } else {
            // Criptografado → texto claro: descriptografa os dados atuais antes de salvar
            String decryptedName  = illness.getDecryptedName();
            String decryptedNotes = illness.getDecryptedNotes();
            illness.setIsSensitive(false);
            illness.setIllnessName(decryptedName);
            illness.setNotes(decryptedNotes);
        }
    }

    private Illness findOrThrow(Integer id) {
        return illnessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Doença não encontrada com ID: " + id));
    }

    private void ensureUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
    }

    private boolean canAccessSensitiveData(Authentication authentication, Integer targetUserId) {
        if (authentication == null) return false;
        UserRole role = extractRole(authentication);
        if (role == null) return false;

        return switch (role) {
            case DOCTOR  -> true;
            case PATIENT -> isOwner(authentication, targetUserId);
            default      -> false; // RESCUER, ADMIN, POLICE, FIREFIGHTER sem acesso direto
        };
    }

    private boolean isOwner(Authentication authentication, Integer targetUserId) {
        return userRepository.findByEmail(authentication.getName())
                .map(u -> u.getId().equals(targetUserId))
                .orElse(false);
    }

    private UserRole extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> {
                    try {
                        return UserRole.valueOf(auth.getAuthority().replace("ROLE_", ""));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}