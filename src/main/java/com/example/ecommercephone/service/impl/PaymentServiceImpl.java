package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.PaymentRequest;
import com.example.ecommercephone.entity.Payment;
import com.example.ecommercephone.repository.PaymentRepository;
import com.example.ecommercephone.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getPaymentsByProfileId(Long profileId) {
        return paymentRepository.findByProfileId(profileId);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Payment createPayment(Long profileId, PaymentRequest request) {
        if (request.isDefault()) {
            handleDefaultPayment(profileId);
        }

        Payment payment = Payment.builder()
                .profileId(profileId)
                .method(request.getMethod())
                .accountNumber(request.getAccountNumber())
                .isDefault(request.isDefault())
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment updatePayment(Long paymentId, Long profileId, PaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phương thức thanh toán"));

        if (!payment.getProfileId().equals(profileId)) {
            throw new IllegalStateException("Bạn không có quyền chỉnh sửa phương thức này");
        }

        if (request.isDefault() && !payment.isDefault()) {
            handleDefaultPayment(profileId);
        }

        payment.setMethod(request.getMethod());
        payment.setAccountNumber(request.getAccountNumber());
        payment.setDefault(request.isDefault());

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId, Long profileId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phương thức thanh toán"));

        if (!payment.getProfileId().equals(profileId)) {
            throw new IllegalStateException("Bạn không có quyền xóa phương thức này");
        }

        paymentRepository.delete(payment);
    }

    @Override
    @Transactional
    public void setDefaultPayment(Long paymentId, Long profileId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phương thức thanh toán"));

        if (!payment.getProfileId().equals(profileId)) {
            throw new IllegalStateException("Bạn không có quyền cập nhật phương thức này");
        }

        handleDefaultPayment(profileId); // bổ sung try-catch
        payment.setDefault(true);
        paymentRepository.save(payment);
    }

    private void handleDefaultPayment(Long profileId) {
        paymentRepository.findByProfileIdAndIsDefault(profileId, true)
                .ifPresent(p -> {
                    p.setDefault(false);
                    paymentRepository.save(p);
                });
    }
}
