package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.PaymentRequest;
import com.example.ecommercephone.entity.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getPaymentsByProfileId(Long profileId);
    Payment getPaymentById(Long id);
    Payment createPayment(Long profileId, PaymentRequest request);
    Payment updatePayment(Long paymentId, Long profileId, PaymentRequest request);
    void deletePayment(Long paymentId, Long profileId);
    void setDefaultPayment(Long paymentId, Long profileId);
}
