package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.OrderHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);
}

