package com.help.tap.controller;

import com.help.tap.dto.deficiency.DeficiencyCreateDTO;
import com.help.tap.dto.deficiency.DeficiencyResponseDTO;
import com.help.tap.dto.deficiency.DeficiencyUpdateDTO;
import com.help.tap.service.DeficiencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deficiencies")
@RequiredArgsConstructor
public class DeficiencyController {

    private final DeficiencyService deficiencyService;

    @PostMapping
    public ResponseEntity<DeficiencyResponseDTO> createDeficiency(
            @Valid @RequestBody DeficiencyCreateDTO dto) {
        DeficiencyResponseDTO created = deficiencyService.createDeficiency(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeficiencyResponseDTO> getDeficiencyById(
            @PathVariable Integer id) {
        DeficiencyResponseDTO deficiency = deficiencyService.getDeficiencyById(id);
        return ResponseEntity.ok(deficiency);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeficiencyResponseDTO>> getDeficienciesByUser(
            @PathVariable Integer userId) {
        List<DeficiencyResponseDTO> deficiencies = deficiencyService.getDeficienciesByUser(userId);
        return ResponseEntity.ok(deficiencies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeficiencyResponseDTO> updateDeficiency(
            @PathVariable Integer id,
            @Valid @RequestBody DeficiencyUpdateDTO dto) {
        DeficiencyResponseDTO updated = deficiencyService.updateDeficiency(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeficiency(
            @PathVariable Integer id) {
        deficiencyService.deleteDeficiency(id);
        return ResponseEntity.noContent().build();
    }
}
