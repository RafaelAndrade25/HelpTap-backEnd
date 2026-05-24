package com.help.tap.controller;

import com.help.tap.dto.disorder.DisorderCreateDTO;
import com.help.tap.dto.disorder.DisorderResponseDTO;
import com.help.tap.dto.disorder.DisorderUpdateDTO;
import com.help.tap.service.DisorderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disorders")
@RequiredArgsConstructor
public class DisorderController {

    private final DisorderService disorderService;

    @PostMapping
    public ResponseEntity<DisorderResponseDTO> createDisorder(
            @Valid @RequestBody DisorderCreateDTO dto) throws Exception {
        DisorderResponseDTO created = disorderService.createDisorder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{disorderId}")
    public ResponseEntity<DisorderResponseDTO> getDisorderById(
            @PathVariable Integer disorderId,
            Authentication authentication) throws Exception {
        DisorderResponseDTO disorder = disorderService.getDisorderById(disorderId, authentication);
        return ResponseEntity.ok(disorder);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DisorderResponseDTO>> getDisordersByUser(
            @PathVariable Integer userId,
            Authentication authentication) {
        List<DisorderResponseDTO> disorders = disorderService.getDisordersByUser(userId, authentication);
        return ResponseEntity.ok(disorders);
    }

    @PutMapping("/{disorderId}")
    public ResponseEntity<DisorderResponseDTO> updateDisorder(
            @PathVariable Integer disorderId,
            @Valid @RequestBody DisorderUpdateDTO dto,
            Authentication authentication) throws Exception {
        DisorderResponseDTO updated = disorderService.updateDisorder(disorderId, dto, authentication);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{disorderId}")
    public ResponseEntity<Void> deleteDisorder(
            @PathVariable Integer disorderId,
            Authentication authentication) {
        disorderService.deleteDisorder(disorderId, authentication);
        return ResponseEntity.noContent().build();
    }
}