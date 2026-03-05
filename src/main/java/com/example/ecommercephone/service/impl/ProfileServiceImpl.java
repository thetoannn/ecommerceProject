package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.ProfileUpdateRequest;
import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.repository.ProfileRepository;
import com.example.ecommercephone.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Profile findByAccountUid(String accountUid) {
        return profileRepository.findByAccountUid(accountUid).orElse(null);
    }

    @Override
    @Transactional
    public Profile updateProfile(String username, ProfileUpdateRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản"));

        String accountUid = account.getUid();

        Profile profile = profileRepository.findByAccountUid(accountUid)
                .orElseGet(() -> Profile.builder()
                        .accountUid(accountUid)
                        .build());

        profile.setFullName(normalize(request.getFullName()));
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setPhone(normalize(request.getPhone()));

        return profileRepository.save(profile);
    }

    @Override
    public ProfileUpdateRequest toUpdateRequest(Profile profile) {
        ProfileUpdateRequest form = new ProfileUpdateRequest();
        if (profile != null) {
            form.setFullName(profile.getFullName());
            form.setDateOfBirth(profile.getDateOfBirth());
            form.setPhone(profile.getPhone());
        }
        return form;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}