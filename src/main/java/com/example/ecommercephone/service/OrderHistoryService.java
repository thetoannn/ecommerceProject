package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.response.OrderHistoryResponse;
import com.example.ecommercephone.entity.OrderHistory;
import com.example.ecommercephone.enums.OrderStatus;

import java.util.List;

public interface OrderHistoryService {

    List<OrderHistoryResponse> getOrderHistory(Long orderId);

    void createOrderHistory(Long orderId, OrderStatus oldStatus, OrderStatus newStatus, String changedByUid, String note);

    OrderHistory saveOrderHistory(OrderHistory orderHistory);
}
