package com.help.tap.service;

import com.help.tap.dto.deficiency.DeficiencyCreateDTO;
import com.help.tap.dto.deficiency.DeficiencyResponseDTO;
import com.help.tap.dto.deficiency.DeficiencyUpdateDTO;
import com.help.tap.model.Deficiency;
import com.help.tap.model.User;
import com.help.tap.repository.DeficiencyRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeficiencyService {

    private final DeficiencyRepository deficiencyRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeficiencyResponseDTO createDeficiency(DeficiencyCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        Deficiency deficiency = Deficiency.builder()
                .user(user)
                .type(dto.type())
                .description(dto.description())
                .build();

        return DeficiencyResponseDTO.fromEntity(deficiencyRepository.save(deficiency));
    }

    @Transactional(readOnly = true)
    public DeficiencyResponseDTO getDeficiencyById(Integer id) {
        return DeficiencyResponseDTO.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<DeficiencyResponseDTO> getDeficienciesByUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return deficiencyRepository.findByUser_Id(userId)
                .stream()
                .map(DeficiencyResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DeficiencyResponseDTO updateDeficiency(Integer id, DeficiencyUpdateDTO dto) {
        Deficiency deficiency = findOrThrow(id);

        if (dto.type()        != null) deficiency.setType(dto.type());
        if (dto.description() != null) deficiency.setDescription(dto.description());

        return DeficiencyResponseDTO.fromEntity(deficiencyRepository.save(deficiency));
    }

    @Transactional
    public void deleteDeficiency(Integer id) {
        if (!deficiencyRepository.existsById(id)) {
            throw new EntityNotFoundException("Deficiência não encontrada com ID: " + id);
        }
        deficiencyRepository.deleteById(id);
    }

    private Deficiency findOrThrow(Integer id) {
        return deficiencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Deficiência não encontrada com ID: " + id));
    }
}
