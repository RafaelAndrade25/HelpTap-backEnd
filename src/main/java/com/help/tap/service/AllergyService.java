package com.help.tap.service;

import com.help.tap.dto.allergies.AllergyCreateDTO;
import com.help.tap.dto.allergies.AllergyResponseDTO;
import com.help.tap.dto.allergies.AllergyUpdateDTO;
import com.help.tap.model.Allergies;
import com.help.tap.model.RiskRating;
import com.help.tap.model.User;
import com.help.tap.repository.AllergyRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final UserRepository userRepository;

    @Transactional
    public AllergyResponseDTO createAllergy(AllergyCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        Allergies allergy = Allergies.builder()
                .user(user)
                .allergenic(dto.allergenic())
                .riskRating(dto.riskRating())
                .build();

        return AllergyResponseDTO.fromEntity(allergyRepository.save(allergy));
    }

    @Transactional(readOnly = true)
    public AllergyResponseDTO getAllergyById(Integer allergyId) {
        return AllergyResponseDTO.fromEntity(findOrThrow(allergyId));
    }

    @Transactional(readOnly = true)
    public List<AllergyResponseDTO> getAllergiesByUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return allergyRepository.findByUser_Id(userId)
                .stream()
                .map(AllergyResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AllergyResponseDTO> getAllergiesByUserAndRisk(Integer userId, RiskRating riskRating) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return allergyRepository.findByUser_IdAndRiskRating(userId, riskRating)
                .stream()
                .map(AllergyResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AllergyResponseDTO> getCriticalAllergiesByUser(Integer userId) {
        return allergyRepository.findCriticalByUser_Id(userId)
                .stream()
                .map(AllergyResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AllergyResponseDTO updateAllergy(Integer allergyId, AllergyUpdateDTO dto) {
        Allergies allergy = findOrThrow(allergyId);

        if (dto.allergenic()  != null) allergy.setAllergenic(dto.allergenic());
        if (dto.riskRating()  != null) allergy.setRiskRating(dto.riskRating());

        return AllergyResponseDTO.fromEntity(allergyRepository.save(allergy));
    }

    @Transactional
    public void deleteAllergy(Integer allergyId) {
        if (!allergyRepository.existsById(allergyId)) {
            throw new EntityNotFoundException("Alergia não encontrada com ID: " + allergyId);
        }
        allergyRepository.deleteById(allergyId);
    }

    private Allergies findOrThrow(Integer id) {
        return allergyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Alergia não encontrada com ID: " + id));
    }
}