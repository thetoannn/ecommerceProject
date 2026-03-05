package com.example.ecommercephone.dto.request;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long addressId;
    private Long paymentId;
    private String notes;
}
