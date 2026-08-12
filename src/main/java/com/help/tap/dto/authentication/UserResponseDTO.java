package com.help.tap.dto.authentication;

import com.help.tap.model.UserRole;

import java.time.LocalDate;

public record UserResponseDTO(Integer id,
        String fullName,
        String cpf,
        LocalDate dateBirth,
        String sex,
        String email,
        String nameOfFather,
        String nameOfMother,
        String identifier,
        UserRole role,
        String phone,
        String userPicture,
        Boolean privacyPolicyAccepted,
        Boolean termsOfUseAccepted) {
}
