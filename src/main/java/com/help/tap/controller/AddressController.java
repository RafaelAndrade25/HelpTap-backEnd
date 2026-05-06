package com.help.tap.controller;

import com.help.tap.dto.address.AddressCreateDTO;
import com.help.tap.dto.address.AddressResponseDTO;
import com.help.tap.dto.address.AddressUpdateDTO;
import com.help.tap.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressCreateDTO dto) {
        AddressResponseDTO created = addressService.createAddress(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @PathVariable Integer id) {
        AddressResponseDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUser(
            @PathVariable Integer userId) {
        List<AddressResponseDTO> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Integer id,
            @Valid @RequestBody AddressUpdateDTO dto) {
        AddressResponseDTO updated = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
