package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.ProfileUpdateRequest;
import com.example.ecommercephone.entity.Profile;

public interface ProfileService {

    Profile findByAccountUid(String accountUid);

    Profile updateProfile(String username, ProfileUpdateRequest request);

    ProfileUpdateRequest toUpdateRequest(Profile profile);
}
