package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByAccountUidOrderByCreatedAtDesc(String accountUid);

    Page<Notification> findByAccountUidOrderByCreatedAtDesc(String accountUid, Pageable pageable);

    Long countByAccountUidAndIsReadFalse(String accountUid);
    
    List<Notification> findByAccountUidAndIsReadFalseOrderByCreatedAtDesc(String accountUid);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.accountUid = :accountUid AND n.isRead = false")
    int markAllAsReadByAccountUid(@Param("accountUid") String accountUid);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.accountUid = :accountUid")
    int markAsReadByIdAndAccountUid(@Param("id") Long id, @Param("accountUid") String accountUid);
}
