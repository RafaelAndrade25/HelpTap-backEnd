package com.help.tap.controller;

import com.help.tap.dto.illness.IllnessCreateDTO;
import com.help.tap.dto.illness.IllnessResponseDTO;
import com.help.tap.dto.illness.IllnessUpdateDTO;
import com.help.tap.model.RiskRating;
import com.help.tap.service.IllnessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/illnesses")
@RequiredArgsConstructor
public class IllnessController {

    private final IllnessService illnessService;

    @PostMapping
    public ResponseEntity<IllnessResponseDTO> createIllness(
            @Valid @RequestBody IllnessCreateDTO dto) {
        IllnessResponseDTO created = illnessService.createIllness(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{illnessId}")
    public ResponseEntity<IllnessResponseDTO> getIllnessById(
            @PathVariable Integer illnessId) {
        IllnessResponseDTO illness = illnessService.getIllnessById(illnessId);
        return ResponseEntity.ok(illness);
    }

    /**
     * Lista doenças de um usuário.
     * Aceita filtro opcional por nível de risco:
     *   GET /api/illnesses/user/3            → todas as doenças
     *   GET /api/illnesses/user/3?risk=HIGH  → apenas HIGH e acima
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IllnessResponseDTO>> getIllnessesByUser(
            @PathVariable Integer userId,
            @RequestParam(required = false) RiskRating risk) {

        List<IllnessResponseDTO> illnesses = (risk != null)
                ? illnessService.getIllnessesByUserIdAndRisk(userId, risk)
                : illnessService.getIllnessesByUserId(userId);

        return ResponseEntity.ok(illnesses);
    }

    @PutMapping("/{illnessId}")
    public ResponseEntity<IllnessResponseDTO> updateIllness(
            @PathVariable Integer illnessId,
            @Valid @RequestBody IllnessUpdateDTO dto) {
        IllnessResponseDTO updated = illnessService.updateIllness(illnessId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{illnessId}")
    public ResponseEntity<Void> deleteIllness(
            @PathVariable Integer illnessId) {
        illnessService.deleteIllness(illnessId);
        return ResponseEntity.noContent().build();
    }
}