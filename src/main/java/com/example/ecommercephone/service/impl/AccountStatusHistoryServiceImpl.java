package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.entity.AccountStatusHistory;
import com.example.ecommercephone.repository.AccountStatusHistoryRepository;
import com.example.ecommercephone.service.AccountStatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountStatusHistoryServiceImpl implements AccountStatusHistoryService {
    @Autowired
    private AccountStatusHistoryRepository accountStatusHistoryRepository;

    @Override
    public List<AccountStatusHistory> findByUidOrderByChangedAtDesc(String uid) {
        return accountStatusHistoryRepository.findByUidOrderByChangedAtDesc(uid);
    }
}
