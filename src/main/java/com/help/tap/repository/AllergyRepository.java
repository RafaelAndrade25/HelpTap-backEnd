package com.help.tap.repository;

import com.help.tap.model.Allergies;
import com.help.tap.model.RiskRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergies, Integer> {

    List<Allergies> findByUser_Id(Integer userId);

    List<Allergies> findByUser_IdAndRiskRating(Integer userId, RiskRating riskRating);

    @Query("SELECT a FROM Allergies a WHERE a.user.id = :userId " +
            "AND a.riskRating IN (com.help.tap.model.RiskRating.HIGH, " +
            "                     com.help.tap.model.RiskRating.CRITICAL)")
    List<Allergies> findCriticalByUser_Id(@Param("userId") Integer userId);
}
