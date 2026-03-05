package com.example.ecommercephone.entity;

import com.example.ecommercephone.enums.PaymentMethod;
import com.example.ecommercephone.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.ecommercephone.enums.OrderStatus;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_uid", nullable = false, length = 50)
    private String accountUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    private Account account;

    @Builder.Default
    @Column(name = "order_date")
    private Instant orderDate = Instant.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PROCESSING;

    @Column(name = "total_amount", precision = 20, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "recipient_name",  length = 100)
    private String recipientName;

    @Column(name = "phone", length = 11)
    private String phoneNumber;

    @Column(name = "address_line",  length = 255)
    private String addressLine;

    @Column(name = "city",  length = 100)
    private String city;

    @Column(name = "country",  length = 100)
    private String country;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50, nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50, nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "payment_date", length = 50)
    private Instant paymentDate;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}


