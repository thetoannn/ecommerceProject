package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @EntityGraph(attributePaths = {
        "brand", 
        "images", 
        "productAttribute", 
        "productAttribute.attribute", 
        "productAttribute.attributeValue"
    })
    Optional<Product> findById(Long id);

    @EntityGraph(attributePaths = {"brand", "images"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "images"})
    List<Product> findByStatusAndStockGreaterThan(String status, int stock);

    @EntityGraph(attributePaths = {"brand", "images"})
    Page<Product> findByStatusAndStockGreaterThan(String status, int stock, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "images"})
    List<Product> findByNameContainingIgnoreCaseAndStatusAndStockGreaterThan(String name, String status, int stock);

    @EntityGraph(attributePaths = {"brand", "images"})
    Page<Product> findByNameContainingIgnoreCaseAndStatusAndStockGreaterThan(String name, String status, int stock, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "images"})
    List<Product> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "images"})
    List<Product> findByStatusOrderByPriceDesc(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "images"})
    Page<Product> findByStatusIn(List<String> statuses, Pageable pageable);

    @Query("SELECT p.stock FROM Product p WHERE p.id = :productId")
    Integer findStockById(@Param("productId") Long productId);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stock = p.stock - :qty
        WHERE p.id = :productId AND p.stock >= :qty
    """)
    int decreaseStock(
            @Param("productId") Long productId,
            @Param("qty") int qty
    );

    @Query("""
        SELECT 
            COUNT(p),
            SUM(CASE WHEN p.stock <= 0 THEN 1 ELSE 0 END),
            SUM(CASE WHEN p.stock > 0 AND p.stock <= 5 THEN 1 ELSE 0 END),
            SUM(p.price * CAST(p.stock AS bigdecimal))
        FROM Product p
        WHERE p.status IN :statuses
    """)
    List<Object[]> getProductStatistics(@Param("statuses") List<String> statuses);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stock = p.stock + :qty
        WHERE p.id = :productId
    """)
    int increaseStock(
            @Param("productId") Long productId,
            @Param("qty") int qty
    );
}


