package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderDetailResponse {
    private Order order;
    private List<OrderItemDetailResponse> orderItems;
    private List<OrderHistoryResponse> history;
    private int totalItems;
}
