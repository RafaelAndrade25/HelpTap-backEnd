package com.help.tap.controller;

import com.help.tap.dto.emergencyContact.EmergencyContactCreateDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactResponseDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactUpdateDTO;
import com.help.tap.service.EmergencyContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencyContacts")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @PostMapping
    public ResponseEntity<EmergencyContactResponseDTO> createContact(
            @Valid @RequestBody EmergencyContactCreateDTO dto) {
        EmergencyContactResponseDTO created = emergencyContactService.createContact(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<EmergencyContactResponseDTO> getContactById(
            @PathVariable Integer contactId) {
        EmergencyContactResponseDTO contact = emergencyContactService.getContactById(contactId);
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmergencyContactResponseDTO>> getContactsByUser(
            @PathVariable Integer userId) {
        List<EmergencyContactResponseDTO> contacts = emergencyContactService.getContactsByUserId(userId);
        return ResponseEntity.ok(contacts);
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<EmergencyContactResponseDTO> updateContact(
            @PathVariable Integer contactId,
            @Valid @RequestBody EmergencyContactUpdateDTO dto) {
        EmergencyContactResponseDTO updated = emergencyContactService.updateContact(contactId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Integer contactId) {
        emergencyContactService.deleteContact(contactId);
        return ResponseEntity.noContent().build();
    }
}
