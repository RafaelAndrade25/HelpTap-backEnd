package com.help.tap.service;

import com.help.tap.dto.profile.ProfessionalProfileDTO;
import com.help.tap.dto.profile.PublicProfileDTO;
import com.help.tap.infra.security.EncryptionUtil;
import com.help.tap.model.Address;
import com.help.tap.model.Disorder;
import com.help.tap.model.User;
import com.help.tap.model.UserRole;
import com.help.tap.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServicePermissionsTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private IllnessRepository illnessRepository;
    @Mock
    private DisorderRepository disorderRepository;
    @Mock
    private AllergyRepository allergyRepository;
    @Mock
    private DeficiencyRepository deficiencyRepository;
    @Mock
    private EmergencyContactRepository emergencyContactRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileService profileService;

    private User victim;
    private Disorder disorder;
    private Address address;

    @BeforeEach
    void setUp() {
        victim = User.builder()
                .id(10)
                .fullName("Victim Test")
                .email("victim@test.com")
                .hasHealthInsurance(true)
                .healthInsuranceNumber("PLANO-123456")
                .role(UserRole.PATIENT)
                .build();

        disorder = Disorder.builder()
                .medicalRecordId(1)
                .user(victim)
                .disorderName("enc_autism")
                .disorderDegree("enc_leve")
                .description("enc_desc")
                .build();

        address = Address.builder()
                .id(1)
                .user(victim)
                .street("Rua das Flores")
                .number("123")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .cep("01001-000")
                .build();
    }

    private void mockAuth(UserRole role, String email) {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_" + role.name())))
                .when(authentication).getAuthorities();
    }

    @Test
    @DisplayName("FIREFIGHTER deve visualizar transtornos descriptografados e número do plano de saúde")
    void firefighterShouldViewDecryptedDisordersAndInsurance() throws Exception {
        mockAuth(UserRole.FIREFIGHTER, "bombeiro@helptap.com");
        when(userRepository.findById(10)).thenReturn(Optional.of(victim));
        when(medicalRecordRepository.findByUser_Id(10)).thenReturn(Optional.empty());
        when(illnessRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(disorderRepository.findByUser_Id(10)).thenReturn(List.of(disorder));
        when(allergyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(deficiencyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(emergencyContactRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());

        when(encryptionUtil.decrypt("enc_autism")).thenReturn("Autismo");
        when(encryptionUtil.decrypt("enc_leve")).thenReturn("Leve");
        when(encryptionUtil.decrypt("enc_desc")).thenReturn("Descrição real");

        ProfessionalProfileDTO profile = profileService.buildProfessionalProfile(10, authentication);

        assertNotNull(profile);
        assertTrue(profile.hasHealthInsurance());
        assertEquals("PLANO-123456", profile.healthInsuranceNumber());

        assertEquals(1, profile.disorders().size());
        assertEquals("Autismo", profile.disorders().get(0).disorderName());
        assertEquals("Leve", profile.disorders().get(0).disorderDegree());
        assertEquals("Descrição real", profile.disorders().get(0).description());

        // FIREFIGHTER não deve ver endereço
        assertTrue(profile.addresses().isEmpty());
    }

    @Test
    @DisplayName("RESCUER deve visualizar transtornos descriptografados e número do plano de saúde")
    void rescuerShouldViewDecryptedDisordersAndInsurance() throws Exception {
        mockAuth(UserRole.RESCUER, "socorrista@helptap.com");
        when(userRepository.findById(10)).thenReturn(Optional.of(victim));
        when(medicalRecordRepository.findByUser_Id(10)).thenReturn(Optional.empty());
        when(illnessRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(disorderRepository.findByUser_Id(10)).thenReturn(List.of(disorder));
        when(allergyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(deficiencyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(emergencyContactRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());

        when(encryptionUtil.decrypt("enc_autism")).thenReturn("Autismo");
        when(encryptionUtil.decrypt("enc_leve")).thenReturn("Leve");
        when(encryptionUtil.decrypt("enc_desc")).thenReturn("Descrição real");

        ProfessionalProfileDTO profile = profileService.buildProfessionalProfile(10, authentication);

        assertNotNull(profile);
        assertTrue(profile.hasHealthInsurance());
        assertEquals("PLANO-123456", profile.healthInsuranceNumber());

        assertEquals(1, profile.disorders().size());
        assertEquals("Autismo", profile.disorders().get(0).disorderName());

        // RESCUER não deve ver endereço
        assertTrue(profile.addresses().isEmpty());
    }

    @Test
    @DisplayName("POLICE deve visualizar transtornos descriptografados E endereço da vítima na web")
    void policeShouldViewDecryptedDisordersAndAddresses() throws Exception {
        mockAuth(UserRole.POLICE, "policial@helptap.com");
        when(userRepository.findById(10)).thenReturn(Optional.of(victim));
        when(medicalRecordRepository.findByUser_Id(10)).thenReturn(Optional.empty());
        when(illnessRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(disorderRepository.findByUser_Id(10)).thenReturn(List.of(disorder));
        when(allergyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(deficiencyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(emergencyContactRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(addressRepository.findByUserId(10)).thenReturn(List.of(address));

        when(encryptionUtil.decrypt("enc_autism")).thenReturn("Autismo");
        when(encryptionUtil.decrypt("enc_leve")).thenReturn("Leve");
        when(encryptionUtil.decrypt("enc_desc")).thenReturn("Descrição real");

        ProfessionalProfileDTO profile = profileService.buildProfessionalProfile(10, authentication);

        assertNotNull(profile);
        assertTrue(profile.hasHealthInsurance());
        assertEquals("PLANO-123456", profile.healthInsuranceNumber());

        // Disorders descriptografados
        assertEquals(1, profile.disorders().size());
        assertEquals("Autismo", profile.disorders().get(0).disorderName());

        // Endereço presente para POLICE
        assertEquals(1, profile.addresses().size());
        assertEquals("Rua das Flores", profile.addresses().get(0).street());
        assertEquals("São Paulo", profile.addresses().get(0).city());
    }

    @Test
    @DisplayName("Se usuário não tiver plano de saúde, healthInsuranceNumber deve retornar nulo")
    void userWithoutInsuranceShouldReturnNullNumber() {
        victim.setHasHealthInsurance(false);
        victim.setHealthInsuranceNumber(null);

        mockAuth(UserRole.FIREFIGHTER, "bombeiro@helptap.com");
        when(userRepository.findById(10)).thenReturn(Optional.of(victim));
        when(medicalRecordRepository.findByUser_Id(10)).thenReturn(Optional.empty());
        when(illnessRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(disorderRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(allergyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(deficiencyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(emergencyContactRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());

        ProfessionalProfileDTO profile = profileService.buildProfessionalProfile(10, authentication);

        assertNotNull(profile);
        assertFalse(profile.hasHealthInsurance());
        assertNull(profile.healthInsuranceNumber());
    }

    @Test
    @DisplayName("PublicProfileDTO também deve retornar informações de convênio médico")
    void publicProfileShouldContainInsuranceInfo() {
        when(userRepository.findById(10)).thenReturn(Optional.of(victim));
        when(medicalRecordRepository.findByUser_Id(10)).thenReturn(Optional.empty());
        when(allergyRepository.findCriticalByUser_Id(10)).thenReturn(Collections.emptyList());
        when(deficiencyRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());
        when(emergencyContactRepository.findByUser_Id(10)).thenReturn(Collections.emptyList());

        PublicProfileDTO publicProfile = profileService.buildPublicProfile(10);

        assertNotNull(publicProfile);
        assertTrue(publicProfile.hasHealthInsurance());
        assertEquals("PLANO-123456", publicProfile.healthInsuranceNumber());
    }
}
