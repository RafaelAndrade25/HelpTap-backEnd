package com.help.tap.service;

import com.help.tap.dto.address.AddressCreateDTO;
import com.help.tap.dto.address.AddressResponseDTO;
import com.help.tap.dto.address.AddressUpdateDTO;
import com.help.tap.model.Address;
import com.help.tap.model.User;
import com.help.tap.repository.AddressRepository;
import com.help.tap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressResponseDTO createAddress(AddressCreateDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado com ID: " + dto.userId()));

        Address address = Address.builder()
                .user(user)
                .cep(dto.cep())
                .neighborhood(dto.neighborhood())
                .street(dto.street())
                .number(dto.number())
                .city(dto.city())
                .state(dto.state().toUpperCase())
                .build();

        return AddressResponseDTO.fromEntity(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public AddressResponseDTO getAddressById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Endereço não encontrado com ID: " + id));

        return AddressResponseDTO.fromEntity(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        }

        return addressRepository.findByUserId(userId)
                .stream()
                .map(AddressResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AddressResponseDTO updateAddress(Integer id, AddressUpdateDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Endereço não encontrado com ID: " + id));

        if (dto.cep() != null) address.setCep(dto.cep());
        if (dto.neighborhood() != null) address.setNeighborhood(dto.neighborhood());
        if (dto.street() != null) address.setStreet(dto.street());
        if (dto.number() != null) address.setNumber(String.valueOf(dto.number()));
        if (dto.city() != null) address.setCity(dto.city());
        if (dto.state() != null) address.setState(dto.state().toUpperCase());

        return AddressResponseDTO.fromEntity(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Integer id) {
        if (!addressRepository.existsById(id)) {
            throw new EntityNotFoundException("Endereço não encontrado com ID: " + id);
        }
        addressRepository.deleteById(id);
    }
}
