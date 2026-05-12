package com.help.tap.service;

import com.help.tap.dto.emergencyContact.EmergencyContactCreateDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactResponseDTO;
import com.help.tap.dto.emergencyContact.EmergencyContactUpdateDTO;
import com.help.tap.model.EmergencyContact;
import com.help.tap.model.User;
import com.help.tap.repository.EmergencyContactRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;

    @Transactional
    public EmergencyContactResponseDTO createContact(EmergencyContactCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found for ID: " + dto.userId()));

        EmergencyContact contact = EmergencyContact.builder()
                .user(user)
                .phone(dto.phone())
                .phoneOwner(dto.phoneOwner())
                .degreeOfKinship(dto.degreeOfKinship())
                .build();

        return EmergencyContactResponseDTO.fromEntity(emergencyContactRepository.save(contact));
    }

    @Transactional(readOnly = true)
    public EmergencyContactResponseDTO getContactById(Integer contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Emergency contact not found for ID: " + contactId));

        return EmergencyContactResponseDTO.fromEntity(contact);
    }

    @Transactional(readOnly = true)
    public List<EmergencyContactResponseDTO> getContactsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found for ID: " + userId);
        }

        return emergencyContactRepository.findByUser_Id(userId)
                .stream()
                .map(EmergencyContactResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public EmergencyContactResponseDTO updateContact(Integer contactId, EmergencyContactUpdateDTO dto) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Emergency contact not found for ID: " + contactId));

        if (dto.phone()           != null) contact.setPhone(dto.phone());
        if (dto.phoneOwner()      != null) contact.setPhoneOwner(dto.phoneOwner());
        if (dto.degreeOfKinship() != null) contact.setDegreeOfKinship(dto.degreeOfKinship());

        return EmergencyContactResponseDTO.fromEntity(emergencyContactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(Integer contactId) {
        if (!emergencyContactRepository.existsById(contactId)) {
            throw new EntityNotFoundException(
                    "Emergency contact not found for ID: " + contactId);
        }
        emergencyContactRepository.deleteById(contactId);
    }
}
