package com.help.tap.controller;

import com.help.tap.dto.wearable.WearableCreateDTO;
import com.help.tap.dto.wearable.WearableResponseDTO;
import com.help.tap.dto.wearable.WearableUpdateDTO;
import com.help.tap.service.WearableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wearables")
@RequiredArgsConstructor
public class WearableController {

    private final WearableService wearableService;

    @PostMapping
    public ResponseEntity<WearableResponseDTO> createWearable(
            @Valid @RequestBody WearableCreateDTO dto) {
        WearableResponseDTO created = wearableService.createWearable(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WearableResponseDTO> getWearableById(
            @PathVariable Integer id) {
        WearableResponseDTO wearable = wearableService.getWearableById(id);
        return ResponseEntity.ok(wearable);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WearableResponseDTO>> getWearablesByUser(
            @PathVariable Integer userId) {
        List<WearableResponseDTO> wearables = wearableService.getWearablesByUserId(userId);
        return ResponseEntity.ok(wearables);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WearableResponseDTO> updateWearable(
            @PathVariable Integer id,
            @Valid @RequestBody WearableUpdateDTO dto) {
        WearableResponseDTO updated = wearableService.updateWearable(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Inverte o status ativo/inativo da pulseira sem corpo na requisição.
     * Semântica PATCH — altera apenas um aspecto do recurso.
     *
     * POST /api/wearables/5/status  →  ativa se estava inativa, e vice-versa
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<WearableResponseDTO> toggleStatus(
            @PathVariable Integer id) {
        WearableResponseDTO updated = wearableService.toggleStatus(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWearable(
            @PathVariable Integer id) {
        wearableService.deleteWearable(id);
        return ResponseEntity.noContent().build();
    }
}
