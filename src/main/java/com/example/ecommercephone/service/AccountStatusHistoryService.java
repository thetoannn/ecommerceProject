package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.AccountStatusHistory;

import java.util.List;

public interface AccountStatusHistoryService {
    List<AccountStatusHistory> findByUidOrderByChangedAtDesc(String uid);
}
