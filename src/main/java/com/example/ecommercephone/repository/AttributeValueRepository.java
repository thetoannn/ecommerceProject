package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {
    List<AttributeValue> findByAttributeId(Integer attributeId);
    boolean existsByAttributeIdAndValue(Integer attributeId, String value);
}

