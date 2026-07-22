package com.help.tap.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressCreateDTO(@NotNull(message = "O ID do usuário é obrigatório")
                               Integer userId,

                               @NotBlank(message = "O CEP é obrigatório")
                               @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000")
                               String cep,

                               @NotBlank(message = "O bairro é obrigatório")
                               @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
                               String neighborhood,

                               @NotBlank(message = "O logradouro é obrigatório")
                               @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres")
                               String street,

                               @NotBlank(message = "O número é obrigatório")
                               @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
                               String number,

                               @NotBlank(message = "A cidade é obrigatória")
                               @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
                               String city,

                               @NotBlank(message = "O estado é obrigatório")
                               @Size(min = 2, max = 2, message = "O estado deve ser a sigla com 2 caracteres (ex: SP)")
                               String state
) {}
