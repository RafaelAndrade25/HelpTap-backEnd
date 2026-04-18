package com.help.tap.client;

import com.help.tap.dto.external.ValidationRequestDTO;
import com.help.tap.dto.external.ValidationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class CredentialValidationClient {

    @Value("${credential.validation.api.url}")
    private String apiUrl;

    @Value("${credential.validation.api.key}")
    private String apiKey;

    @Value("${credential.validation.enabled:true}")
    private Boolean validationEnabled;

    private final RestClient restClient = RestClient.create();

    public ValidationResponseDTO validate(String credential, String uf, String type) {
        // Se validação desabilitada (ambiente dev), retorna mock
        if (!validationEnabled) {
            log.warn("Validação de credenciais desabilitada - APENAS DESENVOLVIMENTO!");
            return mockValidResponse(credential, uf, type);
        }

        try {
            log.info("Chamando API de validação - Credential: {} - UF: {} - Type: {}",
                    credential, uf, type);

            ValidationRequestDTO request = new ValidationRequestDTO(credential, uf, type);

            ValidationResponseDTO response = restClient.post()
                    .uri(apiUrl)
                    .header("X-API-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(ValidationResponseDTO.class);

            if (response == null) {
                throw new IllegalArgumentException("Response is empty from validation api");
            }

            if (!response.valid()) {
                String errorMsg = response.message() != null
                        ? response.message()
                        : "Invalid Credential";
                throw new IllegalArgumentException(errorMsg);
            }

            log.info("Success: {} - {}", credential, response.name());
            return response;

        } catch (RestClientException e) {
            log.error("Error on call API validation", e);
            throw new IllegalArgumentException(
                    "Unable to validate a credential. Check if the validation API is running on  " +
                            "(port 8081) and try again.", e
            );
        }
    }

    /**
     * Mock de resposta válida para desenvolvimento
     */
    private ValidationResponseDTO mockValidResponse(String credential, String uf, String type) {
        return new ValidationResponseDTO(
                true,
                credential,
                uf,
                type,
                "Mock User - Validação Desabilitada",
                "Mock Specialty",
                "ACTIVE",
                "Validação desabilitada - modo desenvolvimento"
        );
    }
}
