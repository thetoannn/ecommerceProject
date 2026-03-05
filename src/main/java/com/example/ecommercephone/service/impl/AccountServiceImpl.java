package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.RegisterRequest;
import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.enums.AccountStatus;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.repository.OrderRepository;
import com.example.ecommercephone.repository.ProfileRepository;
import com.example.ecommercephone.service.AccountService;
import com.example.ecommercephone.specification.AccountSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    @SuppressWarnings("null")
    public Account register(RegisterRequest request) throws Exception {
        if (usernameExists(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (emailExists(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .status(AccountStatus.ACTIVE)
                .build();
        Account savedAccount = accountRepository.save(account);

        Profile profile = Profile.builder()
                .accountUid(savedAccount.getUid())
                .build();
        profileRepository.save(profile);


        return savedAccount;
    }

    @Override
    public boolean emailExists(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean usernameExists(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    @Override
    public Page<Account> searchAccounts(String query, String role, String status, Pageable pageable) {
        return accountRepository.findAll(
                AccountSpecification.filter(query, role, status),
                pageable
        );
    }

    @Override
    public long countTotalUsers() {
        return accountRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return accountRepository.countByStatus(AccountStatus.ACTIVE);
    }

    @Override
    public long countBlockedUsers() {
        return accountRepository.countByStatus(AccountStatus.BLOCKED);
    }

    @Override
    public long countAdminUsers() {
        return accountRepository.countByRole("ADMIN");
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Account findByUid(String uid) {
        return accountRepository.findByUid(uid)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy accountUser với uid: " + uid));
    }

    @Override
    public boolean hasOrder(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Không tìm thấy accountUser: " + accountId));

        return orderRepository
                .existsByAccountUidOrderByOrderDateDesc(account.getUid());
    }

    @Override
    public Account toggleStatus(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException("không tìm thấy accountUser: " + id));

        boolean hasOrder = orderRepository.existsByAccountUidOrderByOrderDateDesc(account.getUid());

        if (hasOrder) {
            throw new IllegalStateException("Tài khoản hiện đang có đơn hàng!");
        }

        AccountStatus oldStatus = account.getStatus();
        AccountStatus newStatus = (oldStatus == AccountStatus.ACTIVE)
                ? AccountStatus.BLOCKED
                : AccountStatus.ACTIVE;

        account.setStatus(newStatus);
        return accountRepository.save(account);
    }
}

