package com.example.ecommercephone.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductStatistics {
    private final long totalProducts;
    private final long outOfStock;
    private final long lowStock;
    private final BigDecimal inventoryValue;
}
