package com.help.tap.repository;

import com.help.tap.model.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Integer> {

    List<EmergencyContact> findByUser_Id(Integer userId);

    boolean existsByContactIdAndUser_Id(Integer contactId, Integer userId);
}