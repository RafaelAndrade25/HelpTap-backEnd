package com.help.tap.service;

import com.help.tap.dto.medicalRecord.MedicalRecordCreateDTO;
import com.help.tap.dto.medicalRecord.MedicalRecordResponseDTO;
import com.help.tap.dto.medicalRecord.MedicalRecordUpdateDTO;
import com.help.tap.exception.BusinessRuleException;
import com.help.tap.model.MedicalRecord;
import com.help.tap.model.User;
import com.help.tap.repository.MedicalRecordRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecordResponseDTO createMedicalRecord(MedicalRecordCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        // REGRA: cada usuário possui exatamente UMA ficha médica
        if (medicalRecordRepository.existsByUser_Id(dto.userId())) {
            throw new BusinessRuleException(
                    "O usuário ID " + dto.userId() + " já possui uma ficha médica cadastrada. " +
                            "Use PUT /api/medicalRecords/" + dto.userId() + " para atualizá-la.");
        }

        MedicalRecord record = MedicalRecord.builder()
                .user(user)
                .bloodType(dto.bloodType().toUpperCase())
                .height(dto.height())
                .weight(dto.weight())
                .ethnicity(dto.ethnicity())
                .organDonor(dto.organDonor())
                .description(dto.description())
                .build();

        return MedicalRecordResponseDTO.fromEntity(medicalRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getMedicalRecordById(Integer medicalRecordId) {
        return MedicalRecordResponseDTO.fromEntity(findByIdOrThrow(medicalRecordId));
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getMedicalRecordByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return MedicalRecordResponseDTO.fromEntity(
                medicalRecordRepository.findByUser_Id(userId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Nenhuma ficha médica encontrada para o usuário ID: " + userId)));
    }

    @Transactional
    public MedicalRecordResponseDTO updateMedicalRecord(Integer medicalRecordId,
            MedicalRecordUpdateDTO dto) {
        MedicalRecord record = findByIdOrThrow(medicalRecordId);

        if (dto.bloodType() != null)
            record.setBloodType(dto.bloodType().toUpperCase());
        if (dto.height() != null)
            record.setHeight(dto.height());
        if (dto.weight() != null)
            record.setWeight(dto.weight());
        if (dto.ethnicity() != null)
            record.setEthnicity(dto.ethnicity());
        if (dto.organDonor() != null)
            record.setOrganDonor(dto.organDonor());
        if (dto.description() != null)
            record.setDescription(dto.description());

        return MedicalRecordResponseDTO.fromEntity(medicalRecordRepository.save(record));
    }

    @Transactional
    public void deleteMedicalRecord(Integer medicalRecordId) {
        if (!medicalRecordRepository.existsById(medicalRecordId)) {
            throw new EntityNotFoundException(
                    "Ficha médica não encontrada com ID: " + medicalRecordId);
        }
        medicalRecordRepository.deleteById(medicalRecordId);
    }

    private MedicalRecord findByIdOrThrow(Integer id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ficha médica não encontrada com ID: " + id));
    }
}