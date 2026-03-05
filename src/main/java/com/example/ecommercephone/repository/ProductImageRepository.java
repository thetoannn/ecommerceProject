package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderByIsPrimaryDescCreatedAtAsc(Long productId);
    long countByProductId(Long productId);
}

