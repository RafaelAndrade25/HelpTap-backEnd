package com.help.tap.service;

import com.help.tap.dto.disorder.DisorderResponseDTO;
import com.help.tap.dto.disorder.DisorderUpdateDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.infra.security.EncryptionUtil;
import com.help.tap.model.Disorder;
import com.help.tap.model.User;
import com.help.tap.model.UserRole;
import com.help.tap.repository.DisorderRepository;
import com.help.tap.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisorderServicePermissionsTest {

    @Mock
    private DisorderRepository disorderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private DisorderService disorderService;

    private User patient;
    private Disorder disorder;

    @BeforeEach
    void setUp() {
        patient = User.builder()
                .id(20)
                .email("patient@helptap.com")
                .fullName("Paciente Teste")
                .role(UserRole.PATIENT)
                .build();

        disorder = Disorder.builder()
                .medicalRecordId(100)
                .user(patient)
                .disorderName("enc_name")
                .disorderDegree("enc_degree")
                .description("enc_desc")
                .build();
    }

    private void mockAuth(UserRole role, String email) {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_" + role.name())))
                .when(authentication).getAuthorities();
    }

    @Test
    @DisplayName("FIREFIGHTER pode visualizar transtorno descriptografado por ID")
    void firefighterCanViewDisorderById() throws Exception {
        mockAuth(UserRole.FIREFIGHTER, "bombeiro@helptap.com");
        when(disorderRepository.findById(100)).thenReturn(Optional.of(disorder));
        when(encryptionUtil.decrypt("enc_name")).thenReturn("TDAH");
        when(encryptionUtil.decrypt("enc_degree")).thenReturn("Moderado");
        when(encryptionUtil.decrypt("enc_desc")).thenReturn("Detalhes");

        DisorderResponseDTO response = disorderService.getDisorderById(100, authentication);

        assertNotNull(response);
        assertEquals("TDAH", response.disorderName());
        assertFalse(response.sensitive());
    }

    @Test
    @DisplayName("POLICE pode listar transtornos descriptografados de um usuário")
    void policeCanListDisordersByUser() throws Exception {
        mockAuth(UserRole.POLICE, "policial@helptap.com");
        when(userRepository.existsById(20)).thenReturn(true);
        when(disorderRepository.findByUser_Id(20)).thenReturn(List.of(disorder));
        when(encryptionUtil.decrypt("enc_name")).thenReturn("Bipolaridade");
        when(encryptionUtil.decrypt("enc_degree")).thenReturn("Grave");
        when(encryptionUtil.decrypt("enc_desc")).thenReturn("Detalhes");

        List<DisorderResponseDTO> list = disorderService.getDisordersByUser(20, authentication);

        assertEquals(1, list.size());
        assertEquals("Bipolaridade", list.get(0).disorderName());
        assertFalse(list.get(0).sensitive());
    }

    @Test
    @DisplayName("FIREFIGHTER não pode atualizar transtorno (apenas visualização)")
    void firefighterCannotUpdateDisorder() {
        mockAuth(UserRole.FIREFIGHTER, "bombeiro@helptap.com");
        when(disorderRepository.findById(100)).thenReturn(Optional.of(disorder));

        DisorderUpdateDTO updateDTO = new DisorderUpdateDTO("Novo Nome", "Novo Grau", "Nova Desc");

        assertThrows(BusinessRuleException.class, () ->
                disorderService.updateDisorder(100, updateDTO, authentication));
    }

    @Test
    @DisplayName("POLICE não pode excluir transtorno")
    void policeCannotDeleteDisorder() {
        mockAuth(UserRole.POLICE, "policial@helptap.com");
        when(disorderRepository.findById(100)).thenReturn(Optional.of(disorder));

        assertThrows(BusinessRuleException.class, () ->
                disorderService.deleteDisorder(100, authentication));
    }
}
