package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByProfileId(Long profileId);
    Optional<Payment> findByProfileIdAndIsDefault(Long profileId, boolean isDefault);
}


