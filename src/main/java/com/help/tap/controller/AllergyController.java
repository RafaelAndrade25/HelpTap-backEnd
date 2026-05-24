package com.help.tap.controller;

import com.help.tap.dto.allergies.AllergyCreateDTO;
import com.help.tap.dto.allergies.AllergyResponseDTO;
import com.help.tap.dto.allergies.AllergyUpdateDTO;
import com.help.tap.model.RiskRating;
import com.help.tap.service.AllergyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping
    public ResponseEntity<AllergyResponseDTO> createAllergy(
            @Valid @RequestBody AllergyCreateDTO dto) {
        AllergyResponseDTO created = allergyService.createAllergy(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{allergyId}")
    public ResponseEntity<AllergyResponseDTO> getAllergyById(
            @PathVariable Integer allergyId) {
        AllergyResponseDTO allergy = allergyService.getAllergyById(allergyId);
        return ResponseEntity.ok(allergy);
    }

    // Lista completa — requer autenticação (configurar no SecurityConfig)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AllergyResponseDTO>> getAllergiesByUser(
            @PathVariable Integer userId) {
        List<AllergyResponseDTO> allergies = allergyService.getAllergiesByUser(userId);
        return ResponseEntity.ok(allergies);
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<AllergyResponseDTO>> getAllergiesByRisk(
            @PathVariable Integer userId,
            @RequestParam RiskRating risk) {
        List<AllergyResponseDTO> allergies = allergyService.getAllergiesByUserAndRisk(userId, risk);
        return ResponseEntity.ok(allergies);
    }

    @GetMapping("/user/{userId}/critical")
    public ResponseEntity<List<AllergyResponseDTO>> getCriticalAllergies(
            @PathVariable Integer userId) {
        List<AllergyResponseDTO> critical = allergyService.getCriticalAllergiesByUser(userId);
        return ResponseEntity.ok(critical);
    }

    @PutMapping("/{allergyId}")
    public ResponseEntity<AllergyResponseDTO> updateAllergy(
            @PathVariable Integer allergyId,
            @Valid @RequestBody AllergyUpdateDTO dto) {
        AllergyResponseDTO updated = allergyService.updateAllergy(allergyId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{allergyId}")
    public ResponseEntity<Void> deleteAllergy(
            @PathVariable Integer allergyId) {
        allergyService.deleteAllergy(allergyId);
        return ResponseEntity.noContent().build();
    }
}