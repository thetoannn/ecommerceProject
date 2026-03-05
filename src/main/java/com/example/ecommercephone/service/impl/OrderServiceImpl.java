package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.response.*;
import com.example.ecommercephone.entity.*;
import com.example.ecommercephone.enums.OrderStatus;
import com.example.ecommercephone.repository.*;
import com.example.ecommercephone.specification.OrderSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import com.example.ecommercephone.service.NotificationService;
import com.example.ecommercephone.service.OrderHistoryService;
import com.example.ecommercephone.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryService orderHistoryService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Order> getOrdersByUid(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("UID không được để trống");
        }
        return orderRepository.findByAccountUidOrderByOrderDateDesc(uid);
    }

    @Override
    public Order getOrderDetailForUid(Long orderId, String uid) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));

        if (!order.getAccountUid().equals(uid)) {
            throw new EntityNotFoundException("Không tìm thấy đơn hàng của người dùng");
        }

        return order;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy đơn hàng"));

        List<OrderItemDetailResponse> orderItems = order.getItems().stream()
                .map(item -> {
                    List<OrderItemAttributeRepsonse> attrs = new ArrayList<>();
                    if (item.getProductAttribute() != null && !item.getProductAttribute().isBlank()) {
                        try {
                            attrs = objectMapper.readValue(item.getProductAttribute(),
                                    new TypeReference<List<OrderItemAttributeRepsonse>>() {});
                        } catch (JsonProcessingException ignored) {}
                    }
                    return new OrderItemDetailResponse(
                            item.getId(),
                            item.getProductName(),
                            item.getBrandProduct(),
                            item.getQuantity(),
                            item.getPrice(),
                            attrs
                    );
                })
                .toList();

        return OrderDetailResponse.builder()
                .order(order)
                .orderItems(orderItems)
                .history(orderHistoryService.getOrderHistory(orderId))
                .totalItems(calculateTotalItems(order))
                .build();
    }

    @Override
    public Page<OrderListResponse> searchOrders(String query, int page, int size) {
        String keyword = StringUtils.hasText(query) ? query.trim() : null;
        
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        
        Page<Order> orders = orderRepository.findAll(OrderSpecification.searchByKeyword(keyword), pageRequest);

        return orders.map(order -> new OrderListResponse(
            order.getId(),
            order.getAccountUid(),
            order.getAccount().getUsername(),
            order.getAccount().getEmail(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getOrderDate()
        ));
    }



    @Override
    public int calculateTotalItems(Order order) {
        return order.getItems().stream()
                .map(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .mapToInt(Integer::intValue)
                .sum();
    }


    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String changedByUid, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id: " + orderId));

        OrderStatus oldStatus = order.getStatus();

        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Không thể chuyển trạng thái từ '" +
                    oldStatus.getDisplayName() + "' sang '" + newStatus.getDisplayName() + "'");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        orderHistoryService.createOrderHistory(orderId, oldStatus, newStatus, changedByUid, note);

        if (order.getAccountUid() != null) {
            String statusText = switch (newStatus) {
                case PROCESSING -> "đang được xử lý";
                case SHIPPED -> "đang được giao";
                case DELIVERED -> "đã được giao thành công";
                case CANCELLED -> "đã bị hủy";
            };

            if (newStatus == OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProductId() != null && item.getQuantity() != null) {
                        productRepository.increaseStock(item.getProductId(), item.getQuantity());
                    }
                }
            }

            notificationService.createNotification(
                    order.getAccountUid(),
                    "Cập nhật đơn hàng #" + orderId,
                    "Đơn hàng của bạn " + statusText + "." + (note != null ? " Ghi chú: " + note : ""),
                    Notification.Type.ORDER,
                    "/orders/" + orderId
            );
        }
    }

    @Override
    public long countTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.sumTotalRevenue();
    }
}
