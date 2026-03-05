package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.response.OrderDetailResponse;
import com.example.ecommercephone.dto.response.OrderHistoryResponse;
import com.example.ecommercephone.dto.response.OrderListResponse;
import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    List<Order> getOrdersByUid(String uid);

    Order getOrderDetailForUid(Long orderId, String uid);

    OrderDetailResponse getOrderDetail(Long orderId);

    Page<OrderListResponse> searchOrders(String query, int page, int size);

    int calculateTotalItems(Order order);

    void updateOrderStatus(Long orderId, OrderStatus newStatus, String changedByUid, String note);

    long countTotalOrders();

    long countByStatus(OrderStatus status);

    BigDecimal getTotalRevenue();

}
