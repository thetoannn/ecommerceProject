package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    @Query("SELECT pa FROM ProductAttribute pa JOIN FETCH pa.attribute JOIN FETCH pa.attributeValue WHERE pa.product.id = :productId")
    List<ProductAttribute> findByProductId(@Param("productId") Long productId);
}

