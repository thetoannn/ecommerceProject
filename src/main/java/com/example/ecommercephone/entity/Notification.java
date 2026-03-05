package com.example.ecommercephone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_uid", nullable = false, length = 50)
    private String accountUid;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    public enum Type { ORDER, SYSTEM, PROMOTION }

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Type type = Type.SYSTEM;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = Boolean.FALSE;

    @Column(length = 500)
    private String link;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}


