package com.help.tap.service;

import com.help.tap.dto.wearable.WearableResponseDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.exception.WearableLimitExceededException;
import com.help.tap.model.User;
import com.help.tap.model.Wearable;
import com.help.tap.repository.UserRepository;
import com.help.tap.repository.WearableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WearableServiceSoftDeleteTest {

    @Mock
    private WearableRepository wearableRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WearableService wearableService;

    private User user;
    private Wearable inactiveWearable;
    private Wearable activeWearable;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(wearableService, "maxWearablesPerUser", 5);
        ReflectionTestUtils.setField(wearableService, "baseUrl", "https://helptap-web.vercel.app/pulseira/");

        user = User.builder().id(1).fullName("João Silva").build();

        inactiveWearable = Wearable.builder()
                .id(10)
                .user(user)
                .wearableName("Pulseira Inativa")
                .status(false)
                .accessUrl(UUID.randomUUID())
                .bindingDate(LocalDate.now())
                .deleted(false)
                .build();

        activeWearable = Wearable.builder()
                .id(20)
                .user(user)
                .wearableName("Pulseira Ativa")
                .status(true)
                .accessUrl(UUID.randomUUID())
                .bindingDate(LocalDate.now())
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("Exclusão de pulseira inativa deve realizar soft delete (deleted=true) e não chamar deleteById")
    void deleteInactiveWearableShouldPerformSoftDelete() {
        when(wearableRepository.findByIdAndDeletedFalse(10)).thenReturn(Optional.of(inactiveWearable));
        when(wearableRepository.save(any(Wearable.class))).thenAnswer(invocation -> invocation.getArgument(0));

        wearableService.deleteWearable(10);

        assertTrue(inactiveWearable.getDeleted());
        verify(wearableRepository, times(1)).save(inactiveWearable);
        verify(wearableRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Tentativa de excluir pulseira ativa deve lançar BusinessRuleException (422)")
    void deleteActiveWearableShouldThrowBusinessRuleException() {
        when(wearableRepository.findByIdAndDeletedFalse(20)).thenReturn(Optional.of(activeWearable));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                wearableService.deleteWearable(20));

        assertTrue(ex.getMessage().contains("Não é possível excluir uma pulseira ativa"));
        assertFalse(activeWearable.getDeleted());
        verify(wearableRepository, never()).save(any());
        verify(wearableRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Listagem por usuário deve buscar apenas pulseiras com deleted=false")
    void getWearablesByUserShouldFilterDeleted() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(wearableRepository.findByUser_IdAndDeletedFalse(1)).thenReturn(List.of(activeWearable));

        List<WearableResponseDTO> list = wearableService.getWearablesByUserId(1);

        assertEquals(1, list.size());
        assertEquals("Pulseira Ativa", list.get(0).wearableName());
        verify(wearableRepository, times(1)).findByUser_IdAndDeletedFalse(1);
    }

    @Test
    @DisplayName("getWearableById deve retornar 404 se a pulseira estiver deletada")
    void getWearableByIdShouldReturn404WhenDeleted() {
        when(wearableRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                wearableService.getWearableById(99));
    }

    @Test
    @DisplayName("Cálculo de limite deve considerar apenas pulseiras não deletadas")
    void enforceWearableLimitShouldIgnoreDeleted() {
        when(wearableRepository.countByUser_IdAndDeletedFalse(1)).thenReturn(5);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(WearableLimitExceededException.class, () ->
                wearableService.createWearable(new com.help.tap.dto.wearable.WearableCreateDTO(1, "Nova Pulseira")));
    }
}
