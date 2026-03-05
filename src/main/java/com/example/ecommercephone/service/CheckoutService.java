package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.Order;
import com.example.ecommercephone.enums.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface CheckoutService {

    Order processCheckout(UserDetails principal, Long addressId, Long paymentId, String notes);
}
