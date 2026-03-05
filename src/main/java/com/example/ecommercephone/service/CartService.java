package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.response.CartResponse;
import com.example.ecommercephone.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    void addToCart(String accountUid, Long productId, int quantity);

    void removeFromCart(String accountUid, Long productId);

    void updateQuantity(String accountUid, Long productId, int quantity);

    List<CartItem> getCartItems(String accountUid);

    List<CartItem> getFullCartItems(String accountUid);

    CartResponse getCartResponse(String accountUid);

    void clearCart(String accountUid);

    BigDecimal calculateTotal(List<CartItem> cartItems);
}
