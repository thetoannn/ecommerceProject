package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class OrderListResponse {
    private final Long orderId;
    private final String accountUid;
    private final String username;
    private final String email;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final Instant orderDate;
}
