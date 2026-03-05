package com.example.ecommercephone.repository;

import com.example.ecommercephone.dto.response.OrderListResponse;
import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommercephone.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @EntityGraph(attributePaths = {"account", "items"})
    Optional<Order> findById(Long id);
    @EntityGraph(attributePaths = {"account", "items"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"account", "items"})
    List<Order> findByAccountUidOrderByOrderDateDesc(String accountUid);

    Boolean existsByAccountUidOrderByOrderDateDesc(String accountUid);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal sumTotalRevenue();

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o JOIN o.items oi WHERE oi.productId = :productId AND o.status IN :statuses")
    boolean existsByProductIdAndStatus(@Param("productId") Long productId, @Param("statuses") List<OrderStatus> statuses);
}