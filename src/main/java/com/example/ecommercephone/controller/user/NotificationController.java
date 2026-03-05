package com.example.ecommercephone.controller.user;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Notification;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @GetMapping
    public String notificationsPage(@AuthenticationPrincipal UserDetails principal,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "20") int size,
                                    Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getUserNotifications(account, pageable);
        Long unreadCount = notificationService.getUnreadCount(account);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notifications.getTotalPages());
        
        return "notifications/list";
    }
    
    @GetMapping("/api/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of("count", 0));
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElse(null);
        
        if (account == null) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
        
        Long count = notificationService.getUnreadCount(account);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<List<Notification>> getRecentNotifications(@AuthenticationPrincipal UserDetails principal,
                                                                      @RequestParam(value = "limit", defaultValue = "5") int limit) {
        if (principal == null) {
            return ResponseEntity.ok(List.of());
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElse(null);
        
        if (account == null) {
            return ResponseEntity.ok(List.of());
        }
        
        // Lấy tất cả thông báo gần đây (cả đã đọc và chưa đọc) với limit
        Pageable pageable = PageRequest.of(0, limit);
        Page<Notification> notificationsPage = notificationService.getUserNotifications(account, pageable);

        return ResponseEntity.ok(notificationsPage.getContent());
    }
    
    @PostMapping("/api/mark-read/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> markAsRead(@AuthenticationPrincipal UserDetails principal,
                                                          @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa đăng nhập"));
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElse(null);
        
        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy tài khoản"));
        }
        
        try {
            notificationService.markAsRead(id, account);
            return ResponseEntity.ok(Map.of("success", "true"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/api/mark-all-read")
    @ResponseBody
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa đăng nhập"));
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElse(null);
        
        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy tài khoản"));
        }
        
        try {
            notificationService.markAllAsRead(account);
            return ResponseEntity.ok(Map.of("success", "true"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteNotification(@AuthenticationPrincipal UserDetails principal,
                                                                   @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa đăng nhập"));
        }

        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElse(null);
        
        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy tài khoản"));
        }
        
        try {
            notificationService.deleteNotification(id, account);
            return ResponseEntity.ok(Map.of("success", "true"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
