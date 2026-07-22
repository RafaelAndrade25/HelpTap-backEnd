package com.help.tap.controller;

import com.help.tap.dto.medicalRecord.MedicalRecordCreateDTO;
import com.help.tap.dto.medicalRecord.MedicalRecordResponseDTO;
import com.help.tap.dto.medicalRecord.MedicalRecordUpdateDTO;
import com.help.tap.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicalRecords")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(
            @Valid @RequestBody MedicalRecordCreateDTO dto) {
        MedicalRecordResponseDTO created = medicalRecordService.createMedicalRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Busca pelo ID interno da ficha
    @GetMapping("/{medicalRecordId}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(
            @PathVariable Integer medicalRecordId) {
        MedicalRecordResponseDTO record = medicalRecordService.getMedicalRecordById(medicalRecordId);
        return ResponseEntity.ok(record);
    }

    // Busca pela ficha do usuário — rota mais usada pelo front-end
    @GetMapping("/user/{userId}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordByUser(
            @PathVariable Integer userId) {
        MedicalRecordResponseDTO record = medicalRecordService.getMedicalRecordByUserId(userId);
        return ResponseEntity.ok(record);
    }

    @PutMapping("/{medicalRecordId}")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(
            @PathVariable Integer medicalRecordId,
            @Valid @RequestBody MedicalRecordUpdateDTO dto) {
        MedicalRecordResponseDTO updated = medicalRecordService.updateMedicalRecord(medicalRecordId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{medicalRecordId}")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable Integer medicalRecordId) {
        medicalRecordService.deleteMedicalRecord(medicalRecordId);
        return ResponseEntity.noContent().build();
    }
}
