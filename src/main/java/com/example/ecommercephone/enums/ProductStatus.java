package com.example.ecommercephone.enums;

public enum ProductStatus {
    ACTIVE("Đang bán"),
    INACTIVE("Ngừng bán"),
    OUT_OF_STOCK("Hết hàng"),
    DELETED("Đã xóa");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductStatus fromString(String status) {
        if (status == null) return ACTIVE;
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
