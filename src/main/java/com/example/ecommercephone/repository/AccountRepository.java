package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByUid(String uid);

    long count();

    long countByStatus(AccountStatus status);

    long countByRole(String role);

    List<Account> findByRole(String role);
}