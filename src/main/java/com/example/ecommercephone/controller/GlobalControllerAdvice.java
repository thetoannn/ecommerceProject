package com.example.ecommercephone.controller;

import com.example.ecommercephone.config.CustomUserDetail;
import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.CartItem;
import com.example.ecommercephone.entity.Profile;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.repository.ProfileRepository;
import com.example.ecommercephone.service.CartService;
import com.example.ecommercephone.service.NotificationService;
import com.example.ecommercephone.util.AdminAuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CartService cartService;


    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        String uid = AdminAuthHelper.getCurrentUserUid();
        if (uid == null || uid.isEmpty()) {
            return 0;
        }

        List<CartItem> items = cartService.getCartItems(uid);
        return items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }

    @ModelAttribute("notificationCount")
    public Long notificationCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return 0L;
        }

        Object principal = auth.getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String str) {
            username = str;
        } else {
            return 0L;
        }

        return accountRepository.findByUsername(username)
                .map(notificationService::getUnreadCount)
                .orElse(0L);
    }

    @ModelAttribute("currentProfile")
    public Profile currentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String str) {
            username = str;
        } else {
            return null;
        }

        return accountRepository.findByUsername(username)
                .map(Account::getUid)
                .flatMap(profileRepository::findByAccountUid)
                .orElse(null);
    }

    @ModelAttribute("currentUser")
    public Account currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetail customUserDetail) {
            return accountRepository.findByUsername(customUserDetail.getUsername()).orElse(null);
        }

        return null;
    }
}
