package com.example.ecommercephone.enums;

public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    COMPLETED("Đã thanh toán"),
    FAILED("Thanh toán thất bại");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
