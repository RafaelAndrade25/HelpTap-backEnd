package com.help.tap.repository;

import com.help.tap.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {

    Optional<MedicalRecord> findByUser_Id(Integer userId);

    boolean existsByUser_Id(Integer userId);
}