package com.help.tap.dto.address;

import com.help.tap.model.Address;

public record AddressResponseDTO(
        Integer addressId,
        Integer userId,
        String cep,
        String neighborhood,
        String street,
        Integer number,
        String city,
        String state
) {
    public static AddressResponseDTO fromEntity(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getUser().getId(),
                address.getCep(),
                address.getNeighborhood(),
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState()

        );
    }
}
