package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.response.OrderHistoryResponse;
import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.entity.OrderHistory;
import com.example.ecommercephone.enums.OrderStatus;
import com.example.ecommercephone.repository.AccountRepository;
import com.example.ecommercephone.repository.OrderHistoryRepository;
import com.example.ecommercephone.repository.OrderRepository;
import com.example.ecommercephone.service.OrderHistoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderHistoryResponse> getOrderHistory(Long orderId) {
        List<OrderHistory> historyList = orderHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId);

        return historyList.stream().map(h -> {
            String changedByName = "Hệ thống";
            if (h.getChangedByUid() != null) {
                Account account = accountRepository.findByUid(h.getChangedByUid()).orElse(null);
                if (account != null) {
                    changedByName = account.getUsername();
                }
            }

            return OrderHistoryResponse.builder()
                    .id(h.getId())
                    .oldStatus(h.getOldStatus())
                    .newStatus(h.getNewStatus())
                    .changedByUid(h.getChangedByUid())
                    .changedByName(changedByName)
                    .changedAt(h.getChangedAt())
                    .note(h.getNote())
                    .build();
        }).toList();
    }

    @Override
    public void createOrderHistory(Long orderId, OrderStatus oldStatus, OrderStatus newStatus, String changedByUid, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id: " + orderId));

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedByUid(changedByUid)
                .changedAt(Instant.now())
                .note(note)
                .build();

        orderHistoryRepository.save(history);
    }

    @Override
    public OrderHistory saveOrderHistory(OrderHistory orderHistory) {
        return orderHistoryRepository.save(orderHistory);
    }
}
