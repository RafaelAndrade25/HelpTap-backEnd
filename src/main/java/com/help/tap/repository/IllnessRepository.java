package com.help.tap.repository;

import com.help.tap.model.Illness;
import com.help.tap.model.RiskRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IllnessRepository extends JpaRepository<Illness, Integer> {

    List<Illness> findByUser_Id(Integer userId);

    List<Illness> findByUser_IdAndRiskRating(Integer userId, RiskRating riskRating);

    boolean existsByUser_IdAndIsSensitiveTrue(Integer userId);
}