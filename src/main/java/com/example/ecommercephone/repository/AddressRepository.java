package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByProfileId(Long profileId);
}


