package com.help.tap.service;

import com.help.tap.dto.UserUpdateDTO;
import com.help.tap.dto.authentication.UserCreateDTO;
import com.help.tap.dto.authentication.UserResponseDTO;
import com.help.tap.model.User;
import com.help.tap.model.UserRole;
import com.help.tap.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceHealthInsuranceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve criar usuário com convênio médico persistido")
    void createUserWithHealthInsurance() {
        UserCreateDTO dto = new UserCreateDTO(
                "Maria Souza",
                "12345678901",
                LocalDate.of(1995, 5, 10),
                "FEMININO",
                "maria@test.com",
                "123456",
                "Pai",
                "Mãe",
                "PATIENT",
                "12345678901",
                UserRole.PATIENT,
                "+5511999998888",
                null,
                true,
                true,
                null,
                null,
                null,
                true,
                "UNIMED-998877"
        );

        when(userRepository.existsByEmail("maria@test.com")).thenReturn(false);
        when(userRepository.existsByNationalRegistration("12345678901")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed_pass");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(50);
            return user;
        });

        UserResponseDTO response = userService.createUser(dto);

        assertNotNull(response);
        assertEquals(50, response.id());
        assertTrue(response.hasHealthInsurance());
        assertEquals("UNIMED-998877", response.healthInsuranceNumber());
    }

    @Test
    @DisplayName("Deve atualizar convênio médico no usuário")
    void updateUserHealthInsurance() {
        User existingUser = User.builder()
                .id(50)
                .fullName("Maria Souza")
                .email("maria@test.com")
                .hasHealthInsurance(false)
                .healthInsuranceNumber(null)
                .deleted(false)
                .build();

        when(userRepository.findById(50)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                true,
                "BRADESCO-112233"
        );

        UserResponseDTO response = userService.updateUser(50, updateDTO);

        assertNotNull(response);
        assertTrue(response.hasHealthInsurance());
        assertEquals("BRADESCO-112233", response.healthInsuranceNumber());
    }
}
