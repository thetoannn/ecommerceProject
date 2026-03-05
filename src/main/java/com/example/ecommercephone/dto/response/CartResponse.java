package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {
    private final List<CartItemResponse> items;
    private final BigDecimal total;
    private final int itemCount;
    private final int totalQuantity;

    public static CartResponse from(List<CartItem> cartItems) {
        List<CartItemResponse> items = cartItems.stream()
            .filter(item -> item.getProduct() != null)
            .map(item -> new CartItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity() != null ? item.getQuantity() : 0,
                item.getProduct().getPrice(),
                item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0))
            ))
            .toList();

        int totalQuantity = cartItems.stream()
            .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
            .sum();

        BigDecimal total = cartItems.stream()
            .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, total, items.size(), totalQuantity);
    }
}
