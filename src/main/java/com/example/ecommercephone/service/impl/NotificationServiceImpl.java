package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Notification;
import com.example.ecommercephone.repository.NotificationRepository;
import com.example.ecommercephone.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public Notification createNotification(String accountUid, String title, String message, Notification.Type type) {
        return createNotification(accountUid, title, message, type, null);
    }

    @Override
    @Transactional
    public Notification createNotification(String accountUid, String title, String message, Notification.Type type, String link) {
        Notification notification = Notification.builder()
                .accountUid(accountUid)
                .title(title)
                .message(message)
                .type(type != null ? type : Notification.Type.SYSTEM)
                .link(link)
                .isRead(Boolean.FALSE)
                .build();
        return notificationRepository.save(notification);
    }
    
    @Override
    public List<Notification> getUserNotifications(Account account) {
        return notificationRepository.findByAccountUidOrderByCreatedAtDesc(account.getUid());
    }
    
    @Override
    public Page<Notification> getUserNotifications(Account account, Pageable pageable) {
        return notificationRepository.findByAccountUidOrderByCreatedAtDesc(account.getUid(), pageable);
    }
    
    @Override
    public Long getUnreadCount(Account account) {
        return notificationRepository.countByAccountUidAndIsReadFalse(account.getUid());
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Account account) {
        return notificationRepository.findByAccountUidAndIsReadFalseOrderByCreatedAtDesc(account.getUid());
    }
    
    @Override
    @Transactional
    public void markAsRead(Long notificationId, Account account) {
        notificationRepository.markAsReadByIdAndAccountUid(notificationId, account.getUid());
    }

    @Override
    @Transactional
    public void markAllAsRead(Account account) {
        notificationRepository.markAllAsReadByAccountUid(account.getUid());
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Account account) {
        notificationRepository.findById(notificationId)
                .filter(n -> n.getAccountUid().equals(account.getUid()))
                .ifPresent(notificationRepository::delete);
    }
}
