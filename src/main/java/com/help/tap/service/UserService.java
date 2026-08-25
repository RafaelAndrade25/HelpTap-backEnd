package com.help.tap.service;

import com.help.tap.client.CredentialValidationClient;
import com.help.tap.dto.authentication.UserCreateDTO;
import com.help.tap.dto.authentication.UserResponseDTO;
import com.help.tap.dto.UserUpdateDTO;
import com.help.tap.model.User;
import com.help.tap.model.UserRole;
import com.help.tap.repository.UserRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialValidationClient credentialValidationClient;

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {

        validateUniqueEmail(userCreateDTO.email());
        validateUniqueNationalRegistration(userCreateDTO.cpf());

        if (userCreateDTO.identifier() != null && !userCreateDTO.identifier().isEmpty()) {
            validateUniqueIdentifier(userCreateDTO.identifier());
        }

        // Validação de credenciais para roles do Web Mobile
        if (requiresCredentialValidation(userCreateDTO.role())) {
            validateProfessionalCredential(userCreateDTO.identifier(), userCreateDTO.role());
        }

        if (userRepository.existsByEmail(userCreateDTO.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByNationalRegistration(userCreateDTO.cpf())) {
            throw new IllegalArgumentException("CPF already in use");
        }
        if (userRepository.existsByIdentifier(userCreateDTO.identifier())) {
            throw new IllegalArgumentException("Identifier already in use");
        }

        if (userCreateDTO.dateBirth() != null) {
            int age = java.time.Period.between(userCreateDTO.dateBirth(), java.time.LocalDate.now()).getYears();
            if (age < 18) {
                if (userCreateDTO.legalGuardianName() == null || userCreateDTO.legalGuardianName().isBlank() ||
                    userCreateDTO.legalGuardianCpf() == null || userCreateDTO.legalGuardianCpf().isBlank() ||
                    !Boolean.TRUE.equals(userCreateDTO.legalGuardianConsent())) {
                    throw new IllegalArgumentException("Usuários menores de 18 anos precisam informar o nome, CPF e o consentimento do responsável legal.");
                }
            } else {
                if (userCreateDTO.legalGuardianName() != null || userCreateDTO.legalGuardianCpf() != null || Boolean.TRUE.equals(userCreateDTO.legalGuardianConsent())) {
                    throw new IllegalArgumentException("Usuários com 18 anos ou mais não precisam informar dados de responsável legal.");
                }
            }
        }

        User user = User.builder().fullName(userCreateDTO.fullName())
                .nationalRegistration(userCreateDTO.cpf())
                .birthDate(userCreateDTO.dateBirth())
                .sex(userCreateDTO.sex())
                .email(userCreateDTO.email())
                .password(passwordEncoder.encode(userCreateDTO.password()))
                .fatherName(userCreateDTO.nameOfFather())
                .motherName(userCreateDTO.nameOfMother())
                .identifier(userCreateDTO.identifier())
                .role(userCreateDTO.role())
                // Mapeamento de novos campos no createUser
                .phone(userCreateDTO.phone())
                .userPicture(userCreateDTO.userPicture())
                .privacyPolicyAccepted(
                        userCreateDTO.privacyPolicyAccepted() != null ? userCreateDTO.privacyPolicyAccepted() : false)
                .termsOfUseAccepted(
                        userCreateDTO.termsOfUseAccepted() != null ? userCreateDTO.termsOfUseAccepted() : false)
                .legalGuardianName(userCreateDTO.legalGuardianName())
                .legalGuardianCpf(userCreateDTO.legalGuardianCpf())
                .legalGuardianConsent(
                        userCreateDTO.legalGuardianConsent() != null ? userCreateDTO.legalGuardianConsent() : false)
                .deleted(false)
                .build();
        User savedUser = userRepository.save(user);
        return toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return toResponseDTO(user);

    }

    @Transactional
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return toResponseDTO(user);
    }

    @Transactional
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role)
                .stream()
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Integer id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            user.setEmail(dto.email());
        }

        if (dto.identifier() != null && !dto.identifier().equals(user.getIdentifier())) {
            if (userRepository.existsByIdentifier(dto.identifier())) {
                throw new IllegalArgumentException("Identificador já cadastrado");
            }
            user.setIdentifier(dto.identifier());
        }
        if (dto.fullName() != null)
            user.setFullName(dto.fullName());
        if (dto.dateBirth() != null)
            user.setBirthDate(dto.dateBirth());
        if (dto.sex() != null)
            user.setSex(dto.sex());
        if (dto.password() != null)
            user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.nameOfFather() != null)
            user.setFatherName(dto.nameOfFather());
        if (dto.nameOfMother() != null)
            user.setMotherName(dto.nameOfMother());
        if (dto.role() != null)
            user.setRole(dto.role());

        // Atualização de novos campos
        if (dto.phone() != null)
            user.setPhone(dto.phone());
        if (dto.userPicture() != null)
            user.setUserPicture(dto.userPicture());
        if (dto.privacyPolicyAccepted() != null)
            user.setPrivacyPolicyAccepted(dto.privacyPolicyAccepted());
        if (dto.termsOfUseAccepted() != null)
            user.setTermsOfUseAccepted(dto.termsOfUseAccepted());
        if (dto.legalGuardianName() != null)
            user.setLegalGuardianName(dto.legalGuardianName());
        if (dto.legalGuardianCpf() != null)
            user.setLegalGuardianCpf(dto.legalGuardianCpf());
        if (dto.legalGuardianConsent() != null)
            user.setLegalGuardianConsent(dto.legalGuardianConsent());

        User updatedUser = userRepository.save(user);
        return toResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Integer id, org.springframework.security.core.Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));

        // 1. Correção em tal problema (Permitir apenas ADMIN ou o próprio dono excluir
        // a conta, exclusão lógica para arquivar LGPD)
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !isOwner(authentication, id)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Você não tem permissão para excluir esta conta.");
        }

        user.setDeleted(true);
        // Desvincula email e CPF para permitir novo cadastro sem violar constraint
        // unique
        user.setEmail(user.getEmail() + "_deleted_" + id);
        user.setIdentifier(user.getIdentifier() != null ? user.getIdentifier() + "_del_" + id : null);
        userRepository.save(user);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
    }

    private void validateUniqueNationalRegistration(String nationalRegistration) {
        if (userRepository.existsByNationalRegistration(nationalRegistration)) {
            throw new IllegalArgumentException("National Registration already registered" + nationalRegistration);
        }
    }

    private void validateUniqueIdentifier(String identifier) {
        if (userRepository.existsByIdentifier(identifier)) {
            throw new IllegalArgumentException("Identifier already registered" + identifier);
        }
    }

    private boolean requiresCredentialValidation(UserRole role) {
        return role == UserRole.DOCTOR
                || role == UserRole.POLICE
                || role == UserRole.FIREFIGHTER
                || role == UserRole.RESCUER;
    }

    private void validateProfessionalCredential(String identifier, UserRole role) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException(getCredentialRequiredMessage(role));
        }

        CredentialInfo info = extractCredentialInfo(identifier, role);

        credentialValidationClient.validate(
                info.credential(),
                info.uf(),
                info.type());
    }

    private CredentialInfo extractCredentialInfo(String identifier, UserRole role) {
        return switch (role) {
            case DOCTOR -> extractCrmInfo(identifier);
            case POLICE -> extractPoliceInfo(identifier);
            case FIREFIGHTER -> extractFirefighterInfo(identifier);
            case RESCUER -> extractCorenInfo(identifier);
            default -> throw new IllegalArgumentException("Role does not require validation");
        };
    }

    private CredentialInfo extractCrmInfo(String identifier) {
        String[] parts = identifier.toUpperCase().split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid CRM format. It Must Be: CRM123456-SP ou 123456-SP");
        }

        String crm = parts[0].replace("CRM", "").trim();
        String uf = parts[1].trim();

        if (!crm.matches("\\d+")) {
            throw new IllegalArgumentException("CRM number must contain only digits");
        }

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF must have 2 characters (ex: SP, RJ)");
        }

        return new CredentialInfo(crm, uf, "CRM");
    }

    private CredentialInfo extractCorenInfo(String identifier) {
        String[] parts = identifier.toUpperCase().split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid COREN format. It Must Be: COREN123456-SP ou 123456-SP");
        }

        String coren = parts[0].replace("COREN", "").trim();
        String uf = parts[1].trim();

        if (!coren.matches("\\d+")) {
            throw new IllegalArgumentException("COREN number must contain only digits");
        }

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF must have 2 characters (ex: SP, RJ)");
        }

        return new CredentialInfo(coren, uf, "COREN");
    }

    private CredentialInfo extractPoliceInfo(String identifier) {
        String[] parts = identifier.toUpperCase().split("-");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid Police Functional ID format. It Must Be: POL12345-SSP-SP");
        }

        String uf = parts[parts.length - 1].trim();

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF must have 2 characters (ex: SP, RJ)");
        }

        return new CredentialInfo(identifier.toUpperCase(), uf, "POLICE");
    }

    private CredentialInfo extractFirefighterInfo(String identifier) {
        String[] parts = identifier.toUpperCase().split("-");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid Firefighter Functional ID format. It Must Be: CBM98765-SP");
        }

        String uf = parts[parts.length - 1].trim();

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF must have 2 characters (ex: SP, RJ)");
        }

        return new CredentialInfo(identifier.toUpperCase(), uf, "FIREFIGHTER");
    }

    private String getCredentialRequiredMessage(UserRole role) {
        return switch (role) {
            case DOCTOR -> "CRM is mandatory for doctors. Formato: CRM123456-SP";
            case RESCUER -> "COREN is mandatory for nurses. Formato: COREN123456-SP";
            case POLICE -> "ID Funcional is mandatory for police officers. Formato: POL12345-SSP-SP";
            case FIREFIGHTER -> "ID Funcional is mandatory for firefighters. Formato: CBM98765-SP";
            default -> "Credencial é obrigatória para esta função";
        };
    }

    private record CredentialInfo(String credential, String uf, String type) {
    }

    public boolean isOwner(org.springframework.security.core.Authentication authentication, Integer userId) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }

        String email = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);

        return user != null && user.getEmail().equals(email);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getNationalRegistration(),
                user.getBirthDate(),
                user.getSex(),
                user.getEmail(),
                user.getFatherName(),
                user.getMotherName(),
                user.getIdentifier(),
                user.getRole(),
                // Retornar novos campos no DTO de resposta
                user.getPhone(),
                user.getUserPicture(),
                user.getPrivacyPolicyAccepted(),
                user.getTermsOfUseAccepted(),
                user.getLegalGuardianName(),
                user.getLegalGuardianCpf(),
                user.getLegalGuardianConsent());
    }
}
