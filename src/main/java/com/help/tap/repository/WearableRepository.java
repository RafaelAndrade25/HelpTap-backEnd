package com.help.tap.repository;

import com.help.tap.model.Wearable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WearableRepository extends JpaRepository<Wearable, Integer> {

    // Lista todas as pulseiras ativas/não excluídas de um usuário
    List<Wearable> findByUser_IdAndDeletedFalse(Integer userId);

    // Busca pulseira não excluída por ID
    Optional<Wearable> findByIdAndDeletedFalse(Integer id);

    // Lista todas as pulseiras de um usuário (incluindo histórico)
    List<Wearable> findByUser_Id(Integer userId);

    // Contagem de pulseiras não excluídas por usuário
    int countByUser_IdAndDeletedFalse(Integer userId);

    // Lookup pelo UUID gravado na tag NFC — usado no endpoint de leitura NFC
    Optional<Wearable> findByAccessUrl(UUID accessUrl);

    // Lookup por UUID filtrando apenas não excluídas
    Optional<Wearable> findByAccessUrlAndDeletedFalse(UUID accessUrl);

    // Verifica ownership antes de operações sensíveis
    boolean existsByIdAndUser_Id(Integer id, Integer userId);

    // Garante unicidade do UUID antes de persistir (camada extra de segurança)
    boolean existsByAccessUrl(UUID accessUrl);
}
