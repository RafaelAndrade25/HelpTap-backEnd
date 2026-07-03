package com.help.tap.service;

import com.help.tap.dto.allergies.AllergyResponseDTO;
import com.help.tap.dto.deficiency.DeficiencyResponseDTO;
import com.help.tap.dto.disorder.DisorderResponseDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactResponseDTO;
import com.help.tap.dto.illness.IllnessResponseDTO;
import com.help.tap.dto.profile.MedicalSummaryDTO;
import com.help.tap.dto.profile.ProfessionalProfileDTO;
import com.help.tap.dto.profile.PublicProfileDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.infra.security.EncryptionUtil;
import com.help.tap.model.*;
import com.help.tap.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository            userRepository;
    private final MedicalRecordRepository   medicalRecordRepository;
    private final IllnessRepository         illnessRepository;
    private final DisorderRepository        disorderRepository;
    private final AllergyRepository         allergyRepository;
    private final DeficiencyRepository      deficiencyRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final EncryptionUtil            encryptionUtil;


    @Transactional(readOnly = true)
    public PublicProfileDTO buildPublicProfile(Integer userId) {
        User user = findUserOrThrow(userId);

        String bloodType  = null;
        Boolean organDonor = null;
        var recordOpt = medicalRecordRepository.findByUser_Id(userId);
        if (recordOpt.isPresent()) {
            bloodType  = recordOpt.get().getBloodType();
            organDonor = recordOpt.get().getOrganDonor();
        }

        List<AllergyResponseDTO> criticalAllergies = allergyRepository
                .findCriticalByUser_Id(userId)
                .stream()
                .map(AllergyResponseDTO::fromEntity)
                .toList();

        List<DeficiencyResponseDTO> deficiencies = deficiencyRepository
                .findByUser_Id(userId)
                .stream()
                .map(DeficiencyResponseDTO::fromEntity)
                .toList();

        List<EmergencyContactResponseDTO> emergencyContacts = emergencyContactRepository
                .findByUser_Id(userId)
                .stream()
                .map(EmergencyContactResponseDTO::fromEntity)
                .toList();

        return new PublicProfileDTO(
                userId,
                user.getFullName(),
                bloodType,
                organDonor,
                criticalAllergies,
                deficiencies,
                emergencyContacts
        );
    }


    @Transactional(readOnly = true)
    public ProfessionalProfileDTO buildProfessionalProfile(Integer userId,
                                                           Authentication authentication) {
        User user     = findUserOrThrow(userId);
        UserRole role = extractRole(authentication);

        if (role == null) {
            throw new BusinessRuleException(
                    "Role não identificada. Faça login com uma credencial profissional válida.");
        }
        
        MedicalSummaryDTO medicalSummary = medicalRecordRepository
                .findByUser_Id(userId)
                .map(MedicalSummaryDTO::fromEntity)
                .orElse(null);

        boolean canReadIllness = canReadSensitiveClinical(role, userId, authentication);
        List<IllnessResponseDTO> illnesses = buildIllnessList(userId, canReadIllness);

        boolean canReadDisorder = canReadDisorders(role, userId, authentication);
        List<DisorderResponseDTO> disorders = buildDisorderList(userId, canReadDisorder);

        List<AllergyResponseDTO> allergies = allergyRepository
                .findByUser_Id(userId)
                .stream()
                .map(AllergyResponseDTO::fromEntity)
                .toList();

        List<DeficiencyResponseDTO> deficiencies = deficiencyRepository
                .findByUser_Id(userId)
                .stream()
                .map(DeficiencyResponseDTO::fromEntity)
                .toList();

        List<EmergencyContactResponseDTO> emergencyContacts = emergencyContactRepository
                .findByUser_Id(userId)
                .stream()
                .map(EmergencyContactResponseDTO::fromEntity)
                .toList();

        return new ProfessionalProfileDTO(
                userId,
                user.getFullName(),
                medicalSummary,
                illnesses,
                disorders,
                allergies,
                deficiencies,
                emergencyContacts,
                role.name()
        );
    }

    private List<IllnessResponseDTO> buildIllnessList(Integer userId, boolean canDecrypt) {
        return illnessRepository.findByUser_Id(userId).stream()
                .map(illness -> {
                    illness.setEncryptionUtil(encryptionUtil);
                    try {
                        String name, notes;
                        if (illness.getIsSensitive() && canDecrypt) {
                            name  = illness.getDecryptedName();
                            notes = illness.getDecryptedNotes();
                        } else if (illness.getIsSensitive()) {
                            name  = "[DADOS SENSÍVEIS]";
                            notes = "[DADOS SENSÍVEIS]";
                        } else {
                            name  = illness.getIllnessName();
                            notes = illness.getNotes();
                        }
                        return new IllnessResponseDTO(
                                illness.getIlnessId(),
                                illness.getUser().getId(),
                                illness.getIllnessName(),
                                illness.getIsSensitive(),
                                illness.getNotes(),
                                illness.getRiskRating()

                        );
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Erro ao processar patologia ID: " + illness.getIlnessId(), e);
                    }
                })
                .toList();
    }

    private List<DisorderResponseDTO> buildDisorderList(Integer userId, boolean canDecrypt) {
        return disorderRepository.findByUser_Id(userId).stream()
                .map(disorder -> {
                    try {
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
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Erro ao processar transtorno ID: " + disorder.getMedicalRecordId(), e);
                    }
                })
                .toList();
    }

    private boolean canReadSensitiveClinical(UserRole role, Integer targetUserId,
                                             Authentication auth) {
        return switch (role) {
            case DOCTOR, RESCUER -> true;
            case PATIENT         -> isOwner(auth, targetUserId);
            default              -> false;
        };
    }

    /**
     * Disorders (psiquiátricos/neurodivergências) são restritos a DOCTOR e ao próprio PATIENT.
     * RESCUER, FIREFIGHTER e POLICE não acessam — protocolo de privacidade LGPD.
     * deve ser vizualidada por socorrista tambem
     */
    private boolean canReadDisorders(UserRole role, Integer targetUserId,
                                     Authentication auth) {
        return switch (role) {
            case DOCTOR  -> true;
            case PATIENT -> isOwner(auth, targetUserId);
            default      -> false;
        };
    }

    private boolean isOwner(Authentication auth, Integer targetUserId) {
        return userRepository.findByEmail(auth.getName())
                .map(u -> u.getId().equals(targetUserId))
                .orElse(false);
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

    private User findUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + userId));
    }
}
