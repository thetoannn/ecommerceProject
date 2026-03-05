package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.RegisterRequest;
import com.example.ecommercephone.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    Account register(RegisterRequest request) throws Exception;
    boolean emailExists(String email);
    boolean usernameExists(String username);
    Page<Account> searchAccounts(String query, String role, String status, Pageable pageable);
    long countTotalUsers();
    long countActiveUsers();
    long countBlockedUsers();
    long countAdminUsers();
    Account findByUsername(String username);
    Account findByUid(String uid);
    Account toggleStatus(Long id);
    boolean hasOrder(Long id);
}