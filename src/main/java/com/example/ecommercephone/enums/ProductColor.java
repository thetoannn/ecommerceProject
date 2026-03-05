package com.example.ecommercephone.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ProductColor {

    BLACK("Black", "#000000"),
    WHITE("White", "#FFFFFF"),
    BLUE("Blue", "#007AFF"),
    RED("Red", "#FF3B30"),
    GREEN("Green", "#34C759"),
    YELLOW("Yellow", "#FFCC00"),
    PURPLE("Purple", "#AF52DE"),
    NATURAL_TITANIUM("Natural Titanium", "#8E8E93"),
    ORANGE("Orange", "#FF9500");

    private final String displayName;
    private final String hex;

    ProductColor(String displayName, String hex) {
        this.displayName = displayName;
        this.hex = hex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHex() {
        return hex;
    }

    /**
     * Match từ giá trị attribute (DB) → enum
     */
    public static Optional<ProductColor> fromValue(String value) {
        if (value == null) return Optional.empty();

        return Arrays.stream(values())
                .filter(c -> c.displayName.equalsIgnoreCase(value.trim()))
                .findFirst();
    }

    /**
     * Fallback hex nếu không match
     */
    public static String resolveHex(String value) {
        return fromValue(value)
                .map(ProductColor::getHex)
                .orElse("#CCCCCC");
    }
}
