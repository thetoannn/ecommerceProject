package com.example.ecommercephone.dto.request;

import com.example.ecommercephone.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod method;

    private String accountNumber;

    private boolean isDefault;
}
