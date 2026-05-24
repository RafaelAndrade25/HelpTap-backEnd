package com.help.tap.repository;

import com.help.tap.model.Disorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisorderRepository extends JpaRepository<Disorder, Integer> {

    List<Disorder> findByUser_Id(Integer userId);

    int countByUser_Id(Integer userId);
}