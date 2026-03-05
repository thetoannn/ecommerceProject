package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.AccountStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, Long> {
    List<AccountStatusHistory> findByUidOrderByChangedAtDesc(String uid);
}

