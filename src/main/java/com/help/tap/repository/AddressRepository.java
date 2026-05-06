package com.help.tap.repository;

import com.help.tap.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    List<Address> findByUserId(Integer userId);

    boolean existsByIdAndUserId(Integer id,Integer userId);
}
