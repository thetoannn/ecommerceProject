package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findAll();
    boolean existsByNameIgnoreCase(String name);
}

