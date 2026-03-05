package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Long id;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String changedByUid;
    private String changedByName;
    private Instant changedAt;
    private String note;
}
