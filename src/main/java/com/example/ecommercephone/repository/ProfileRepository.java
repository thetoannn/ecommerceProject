package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAccountUid(String accountUid);
}


