package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class OrderDashboardResponse {
    private final Long id;
    private final String customerEmail;
    private final Instant orderDate;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
}
