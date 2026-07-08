package com.help.tap.controller;

import com.help.tap.dto.nfc.AccessLogResponseDTO;
import com.help.tap.dto.nfc.NfcReadRequestDTO;
import com.help.tap.dto.nfc.NfcReadResponseDTO;
import com.help.tap.service.NfcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nfc")
@RequiredArgsConstructor


public class NfcController {

    private final NfcService nfcService;

    @PostMapping("/read/{uuid}")
    public ResponseEntity<NfcReadResponseDTO> readPublic(
            @PathVariable UUID uuid,
            @RequestBody(required = false) NfcReadRequestDTO request) {

        return ResponseEntity.ok(nfcService.readPublic(uuid, request));
    }

    @PostMapping("/read/{uuid}/professional")
    public ResponseEntity<NfcReadResponseDTO> readProfessional(
            @PathVariable UUID uuid,
            @RequestBody(required = false) NfcReadRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                nfcService.readProfessional(uuid, request, authentication));
    }

    @GetMapping("/logs/wearable/{wearableId}")
    public ResponseEntity<List<AccessLogResponseDTO>> getLogsByWearable(
            @PathVariable Integer wearableId) {
        return ResponseEntity.ok(nfcService.getLogsByWearable(wearableId));
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AccessLogResponseDTO>> getLogsByUser(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(nfcService.getLogsByUser(userId));
    }
}
