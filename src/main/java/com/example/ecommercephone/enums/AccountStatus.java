package com.example.ecommercephone.enums;

public enum AccountStatus {
    ACTIVE("Hoạt động"),
    BLOCKED("Đã khóa"),
//    PENDING("Chờ xác nhận"),
    ;

    private final String displayName;

    AccountStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
