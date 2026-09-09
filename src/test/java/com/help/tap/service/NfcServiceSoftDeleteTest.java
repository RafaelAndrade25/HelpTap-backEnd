package com.help.tap.service;

import com.help.tap.exception.BusinessRuleException;
import com.help.tap.model.User;
import com.help.tap.model.Wearable;
import com.help.tap.repository.AccessLogRepository;
import com.help.tap.repository.WearableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NfcServiceSoftDeleteTest {

    @Mock
    private WearableRepository wearableRepository;
    @Mock
    private AccessLogRepository accessLogRepository;
    @Mock
    private ProfileService profileService;

    @InjectMocks
    private NfcService nfcService;

    private UUID uuid;
    private Wearable deletedWearable;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        User user = User.builder().id(1).fullName("Test").build();

        deletedWearable = Wearable.builder()
                .id(10)
                .user(user)
                .status(false)
                .deleted(true)
                .accessUrl(uuid)
                .build();
    }

    @Test
    @DisplayName("Leitura NFC de pulseira deletada deve lançar BusinessRuleException informando exclusão")
    void readPublicDeletedWearableShouldThrowException() {
        when(wearableRepository.findByAccessUrl(uuid)).thenReturn(Optional.of(deletedWearable));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                nfcService.readPublic(uuid, null));

        assertTrue(ex.getMessage().contains("Esta pulseira foi excluída e não pode ser lida"));
    }
}
