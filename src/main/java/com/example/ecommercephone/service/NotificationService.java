package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification createNotification(String accountUid, String title, String message, Notification.Type type);

    Notification createNotification(String accountUid, String title, String message, Notification.Type type, String link);
    
    List<Notification> getUserNotifications(Account account);
    
    Page<Notification> getUserNotifications(Account account, Pageable pageable);
    
    Long getUnreadCount(Account account);
    
    List<Notification> getUnreadNotifications(Account account);
    
    void markAsRead(Long notificationId, Account account);
    
    void markAllAsRead(Account account);
    
    void deleteNotification(Long notificationId, Account account);
}
