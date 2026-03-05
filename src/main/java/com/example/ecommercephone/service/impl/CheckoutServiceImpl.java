package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.response.OrderItemAttributeRepsonse;
import com.example.ecommercephone.entity.*;
import com.example.ecommercephone.enums.OrderStatus;
import com.example.ecommercephone.enums.PaymentStatus;
import com.example.ecommercephone.repository.*;
import com.example.ecommercephone.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private CartService cartService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrderHistoryService orderHistoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Override
    @Transactional
    public Order processCheckout(UserDetails principal, Long addressId, Long paymentId, String notes) {
        
        Account account = accountRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Vui lòng đăng nhập để đặt hàng"));

        List<CartItem> cartItems = cartService.getFullCartItems(account.getUid());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống");
        }

        Profile profile = profileRepository.findByAccountUid(account.getUid())
                .orElseThrow(() -> new IllegalStateException("Hồ sơ người dùng không tồn tại"));

        Address address = addressService.getAddressById(addressId);
        if (address == null || !address.getProfileId().equals(profile.getId())) {
            throw new IllegalStateException("Địa chỉ không hợp lệ");
        }

        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null || !payment.getProfileId().equals(address.getProfileId())) {
            throw new IllegalStateException("Phương thức thanh toán không hợp lệ");
        }

        BigDecimal totalAmount = cartService.calculateTotal(cartItems);

        Order order = Order.builder()
                .accountUid(account.getUid())
                .orderDate(Instant.now())
                .status(OrderStatus.PROCESSING)
                .totalAmount(totalAmount)
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhone())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .country(address.getCountry())
                .paymentMethod(payment.getMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) continue;

            int qty = cartItem.getQuantity() != null ? cartItem.getQuantity() : 0;
            
            int updated = productRepository.decreaseStock(product.getId(), qty);
            if (updated == 0) {
                throw new IllegalStateException(
                        "Sản phẩm '" + product.getName() + "' không đủ tồn kho"
                );
            }

            Integer remainStock = productRepository.findStockById(product.getId());
            if (remainStock == 0) {
                productService.outOfStockProduct(product.getId());
            }

            List<OrderItemAttributeRepsonse> snapshots =
                    product.getProductAttribute()
                            .stream()
                            .map(pa -> new OrderItemAttributeRepsonse(
                                    pa.getAttribute().getName(),
                                    pa.getAttributeValue().getValue()
                            ))
                            .toList();

            String productAttributeJson;
            try {
                productAttributeJson = objectMapper.writeValueAsString(snapshots);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Không thể serialize product attributes", e);
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(qty)
                    .price(product.getPrice())
                    .productName(product.getName())
                    .brandProduct(product.getBrand() != null ? product.getBrand().getName() : null)
                    .productDescription(product.getDescription())
                    .productAttribute(productAttributeJson)
                    .build();

            order.getItems().add(orderItem);
        }

        orderRepository.save(order);

        orderHistoryService.createOrderHistory(
                order.getId(),
                null,
                OrderStatus.PROCESSING,
                account.getUid(),
                "Đơn hàng khởi tạo"
        );

        notificationService.createNotification(
                account.getUid(),
                "Đơn hàng mới",
                "Đơn hàng #" + order.getId() + " của bạn đã được tạo thành công. Tổng tiền: " +
                        String.format("%,.0f", order.getTotalAmount()) + " ₫",
                Notification.Type.ORDER,
                "/orders/" + order.getId()
        );

        String customerName = account.getUsername();
        for (Account admin : accountRepository.findByRole("ADMIN")) {
            notificationService.createNotification(
                    admin.getUid(),
                    "Đơn hàng mới từ " + customerName,
                    "Đơn hàng #" + order.getId() + " - Tổng tiền: " +
                            String.format("%,.0f", order.getTotalAmount()) + " ₫. " +
                            "Phương thức: " + order.getPaymentMethod().name(),
                    Notification.Type.ORDER,
                    "/admin/orders/" + order.getId()
            );
        }

        cartService.clearCart(account.getUid());

        return order;
    }

}
