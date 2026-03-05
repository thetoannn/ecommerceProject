package com.example.ecommercephone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.ecommercephone.enums.AccountStatus;

import java.time.Instant;

@Entity
@Table(name = "account_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, length = 50)
    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private AccountStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private AccountStatus newStatus;

    @Column(name = "changed_by_uid", length = 50)
    private String changedByUid;

    @Builder.Default
    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    @Column(length = 500)
    private String note;
}

