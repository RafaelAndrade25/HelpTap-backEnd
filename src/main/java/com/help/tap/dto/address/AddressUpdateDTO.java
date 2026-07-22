package com.help.tap.dto.address;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateDTO(@Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000")
                               String cep,

                               @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
                               String neighborhood,

                               @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres")
                               String street,

                              @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
                              String number,

                              @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
                              String city,

                              @Size(min = 2, max = 2, message = "O estado deve ser a sigla com 2 caracteres (ex: SP)")
                              String state
) {}
