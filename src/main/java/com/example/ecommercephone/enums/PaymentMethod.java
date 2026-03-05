package com.example.ecommercephone.enums;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng (COD)", "Thanh toán bằng tiền mặt khi nhận hàng."),
    BANK_TRANSFER("Chuyển khoản ngân hàng", "Chuyển khoản trực tiếp vào tài khoản ngân hàng của chúng tôi."),
    CREDIT_CARD("Thẻ tín dụng", "Thanh toán an toàn bằng thẻ tín dụng Visa, Mastercard."),
    MOMO("Ví MoMo", "Thanh toán nhanh chóng qua ví điện tử MoMo.");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
