package com.help.tap.dto;

import com.help.tap.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDTO(
                @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres") String fullName,

                @Past(message = "Data de nascimento deve ser no passado") LocalDate dateBirth,

                @Pattern(regexp = "^(MASCULINO|FEMININO|OUTRO)$", message = "Sexo deve ser MASCULINO, FEMININO ou OUTRO") String sex,

                @Email(message = "Email deve ser válido") String email,

                @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

                @Size(max = 255, message = "Nome do pai deve ter no máximo 255 caracteres") String nameOfFather,

                @Size(max = 255, message = "Nome da mãe deve ter no máximo 255 caracteres") String nameOfMother,

                @Size(max = 255, message = "Tipo de usuário deve ter no máximo 255 caracteres") String userType,

                @Size(max = 255, message = "Identificador deve ter no máximo 255 caracteres") String identifier,

                UserRole role,

                @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Telefone inválido") String phone,

                String userPicture,

                Boolean privacyPolicyAccepted,

                Boolean termsOfUseAccepted,

                String legalGuardianName,

                @Pattern(regexp = "^([0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2})$", message = "CPF do responsável é inválido") String legalGuardianCpf,

                Boolean legalGuardianConsent,
                Boolean hasHealthInsurance,
                String healthInsuranceNumber) {
}
