package com.help.tap.repository;

import com.help.tap.model.Deficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeficiencyRepository extends JpaRepository<Deficiency, Integer> {

    List<Deficiency> findByUser_Id(Integer userId);
}