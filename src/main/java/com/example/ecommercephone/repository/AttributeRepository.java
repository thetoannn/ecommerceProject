package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    Optional<Attribute> findByName(String name);
    boolean existsByName(String name);
}

