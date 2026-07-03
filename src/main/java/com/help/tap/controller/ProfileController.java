package com.help.tap.controller;

import com.help.tap.dto.profile.ProfessionalProfileDTO;
import com.help.tap.dto.profile.PublicProfileDTO;
import com.help.tap.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}/public")
    public ResponseEntity<PublicProfileDTO> getPublicProfile(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(profileService.buildPublicProfile(userId));
    }

    @GetMapping("/{userId}/professional")
    public ResponseEntity<ProfessionalProfileDTO> getProfessionalProfile(
            @PathVariable Integer userId,
            Authentication authentication) {
        return ResponseEntity.ok(
                profileService.buildProfessionalProfile(userId, authentication));
    }
}