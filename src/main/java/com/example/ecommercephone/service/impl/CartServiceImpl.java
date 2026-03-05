package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.response.CartResponse;
import com.example.ecommercephone.entity.CartItem;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.repository.CartItemRepository;
import com.example.ecommercephone.repository.ProductRepository;
import com.example.ecommercephone.service.CartService;
import com.example.ecommercephone.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public void addToCart(String accountUid, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));

        int currentQtyInCart = getCurrentQuantityByAccount(accountUid, product);
        int totalQty = currentQtyInCart + quantity;

        if (product.getStock() == null || totalQty > product.getStock()) {
            int remaining = product.getStock() != null ? product.getStock() - currentQtyInCart : 0;
            throw new IllegalStateException("Số lượng vượt quá tồn kho. Còn lại: " + remaining + " sản phẩm");
        }

        var existingItem = cartItemRepository.findByAccountUidAndProduct(accountUid, product);
        if (existingItem.isPresent()) {
            var item = existingItem.get();
            int currentQty = item.getQuantity() != null ? item.getQuantity() : 0;
            item.setQuantity(currentQty + quantity);
            cartItemRepository.save(item);
        } else {
            cartItemRepository.save(CartItem.builder()
                    .accountUid(accountUid)
                    .product(product)
                    .quantity(quantity)
                    .build());
        }
    }

    @Override
    @Transactional
    public void removeFromCart(String accountUid, Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;
        cartItemRepository.deleteByAccountUidAndProduct(accountUid, product);
    }

    @Override
    @Transactional
    public void updateQuantity(String accountUid, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));

        if (product.getStock() == null || quantity > product.getStock()) {
            throw new IllegalStateException("Số lượng vượt quá tồn kho. Tối đa: " +
                    (product.getStock() != null ? product.getStock() : 0) + " sản phẩm");
        }

        var existing = cartItemRepository.findByAccountUidAndProduct(accountUid, product);
        if (existing.isPresent()) {
            existing.get().setQuantity(quantity);
            cartItemRepository.save(existing.get());
        } else {
            cartItemRepository.save(CartItem.builder()
                    .accountUid(accountUid)
                    .product(product)
                    .quantity(quantity)
                    .build());
        }
    }

    @Override
    public List<CartItem> getCartItems(String accountUid) {
        return cartItemRepository.findByAccountUidOrderByAddedAtDesc(accountUid);
    }
    
    @Override
    public List<CartItem> getFullCartItems(String accountUid) {
        return cartItemRepository.findFullByAccountUid(accountUid);
    }

    @Override
    public CartResponse getCartResponse(String accountUid) {
        List<CartItem> cartItems = getCartItems(accountUid);
        return CartResponse.from(cartItems);
    }

    @Override
    @Transactional
    public void clearCart(String accountUid) {
        cartItemRepository.deleteByAccountUid(accountUid);
    }

    @Override
    public BigDecimal calculateTotal(List<CartItem> cartItems) {
        if (cartItems == null) return BigDecimal.ZERO;
        return cartItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getCurrentQuantityByAccount(String accountUid, Product product) {
        var existing = cartItemRepository.findQuantityByAccountUidAndProductId(accountUid, product.getId());
        return existing.orElse(0);
    }
}
