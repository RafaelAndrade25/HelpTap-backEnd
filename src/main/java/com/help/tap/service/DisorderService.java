package com.help.tap.service;

import com.help.tap.dto.disorder.DisorderCreateDTO;
import com.help.tap.dto.disorder.DisorderResponseDTO;
import com.help.tap.dto.disorder.DisorderUpdateDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.infra.security.EncryptionUtil;
import com.help.tap.model.Disorder;
import com.help.tap.model.User;
import com.help.tap.model.UserRole;
import com.help.tap.repository.DisorderRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisorderService {

    private final DisorderRepository disorderRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public DisorderResponseDTO createDisorder(DisorderCreateDTO dto) throws Exception {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        // Criptografa todos os campos descritivos antes de persistir
        Disorder disorder = Disorder.builder()
                .user(user)
                .disorderName(encryptionUtil.encrypt(dto.disorderName()))
                .disorderDegree(encryptionUtil.encrypt(dto.disorderDegree()))
                .description(dto.description() != null
                        ? encryptionUtil.encrypt(dto.description())
                        : null)
                .build();

        return toResponseDTO(disorderRepository.save(disorder), true);
    }

    @Transactional(readOnly = true)
    public DisorderResponseDTO getDisorderById(Integer disorderId,
                                               Authentication authentication) throws Exception {
        Disorder disorder = findOrThrow(disorderId);

        boolean canAccess = canAccessSensitiveData(authentication, disorder.getUser().getId());
        if (!canAccess) {
            throw new BusinessRuleException(
                    "Acesso negado: transtornos e neurodivergências são dados sensíveis " +
                            "e requerem credencial médica para visualização.");
        }

        return toResponseDTO(disorder, true);
    }

    @Transactional(readOnly = true)
    public List<DisorderResponseDTO> getDisordersByUser(Integer userId,
                                                        Authentication authentication) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }

        boolean canAccess = canAccessSensitiveData(authentication, userId);

        return disorderRepository.findByUser_Id(userId)
                .stream()
                .map(disorder -> {
                    try {
                        return toResponseDTO(disorder, canAccess);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Erro ao processar dados do transtorno ID: "
                                        + disorder.getMedicalRecordId(), e);
                    }
                })
                .toList();
    }

    @Transactional
    public DisorderResponseDTO updateDisorder(Integer disorderId,
                                              DisorderUpdateDTO dto,
                                              Authentication authentication) throws Exception {
        Disorder disorder = findOrThrow(disorderId);

        if (!canAccessSensitiveData(authentication, disorder.getUser().getId())) {
            throw new BusinessRuleException(
                    "Você não tem permissão para editar dados de transtornos.");
        }

        if (dto.disorderName()   != null)
            disorder.setDisorderName(encryptionUtil.encrypt(dto.disorderName()));
        if (dto.disorderDegree() != null)
            disorder.setDisorderDegree(encryptionUtil.encrypt(dto.disorderDegree()));
        if (dto.description()    != null)
            disorder.setDescription(encryptionUtil.encrypt(dto.description()));

        return toResponseDTO(disorderRepository.save(disorder), true);
    }

    @Transactional
    public void deleteDisorder(Integer disorderId, Authentication authentication) {
        Disorder disorder = findOrThrow(disorderId);

        if (!canAccessSensitiveData(authentication, disorder.getUser().getId())) {
            throw new BusinessRuleException(
                    "Você não tem permissão para excluir dados de transtornos.");
        }

        disorderRepository.deleteById(disorderId);
    }

    private boolean canAccessSensitiveData(Authentication authentication, Integer targetUserId) {
        UserRole role = extractRole(authentication);
        if (role == null) return false;

        return switch (role) {
            case DOCTOR  -> true;
            case PATIENT -> isOwner(authentication, targetUserId);
            default      -> false; // ADMIN, POLICE, FIREFIGHTER, RESCUER sem acesso
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

    private DisorderResponseDTO toResponseDTO(Disorder disorder,
                                              boolean canDecrypt) throws Exception {
        if (canDecrypt) {
            return new DisorderResponseDTO(
                    disorder.getMedicalRecordId(),
                    disorder.getUser().getId(),
                    encryptionUtil.decrypt(disorder.getDisorderName()),
                    encryptionUtil.decrypt(disorder.getDisorderDegree()),
                    disorder.getDescription() != null
                            ? encryptionUtil.decrypt(disorder.getDescription())
                            : null,
                    true
            );
        }

        return new DisorderResponseDTO(
                disorder.getMedicalRecordId(),
                disorder.getUser().getId(),
                "[DADOS SENSÍVEIS]",
                "[DADOS SENSÍVEIS]",
                "[DADOS SENSÍVEIS]",
                true
        );
    }

    private Disorder findOrThrow(Integer id) {
        return disorderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Transtorno não encontrado com ID: " + id));
    }
}