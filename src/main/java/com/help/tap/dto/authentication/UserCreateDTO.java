package com.help.tap.dto.authentication;

import com.help.tap.model.UserRole;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record UserCreateDTO(
        @NotBlank(message = "Nome completo é obrigatório") @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres") String fullName,

        @NotBlank(message = "CPF é obrigatório") @CPF(message = "CPF é inválido") String cpf,

        @Past(message = "Data de nascimento deve ser no passado") LocalDate dateBirth,

        @Pattern(regexp = "^(MASCULINO|FEMININO|OUTRO)$", message = "Sexo deve ser MASCULINO, FEMININO ou OUTRO") String sex,

        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,

        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

        @Size(max = 255, message = "Nome do pai deve ter no máximo 255 caracteres") String nameOfFather,

        @Size(max = 255, message = "Nome da mãe deve ter no máximo 255 caracteres") String nameOfMother,

        @Size(max = 255, message = "Tipo de usuário deve ter no máximo 255 caracteres") String userType,

        @Size(max = 255, message = "Identificador deve ter no máximo 255 caracteres")

        String identifier,

        @NotNull(message = "Role é obrigatória") UserRole role,

        // Novos campos para Create DTO
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Telefone inválido") String phone,

        String userPicture,

        Boolean privacyPolicyAccepted,

        Boolean termsOfUseAccepted,

        String legalGuardianName,

        @CPF(message = "CPF do responsável é inválido") String legalGuardianCpf,

        Boolean legalGuardianConsent,
        Boolean hasHealthInsurance,
        String healthInsuranceNumber) {
}
