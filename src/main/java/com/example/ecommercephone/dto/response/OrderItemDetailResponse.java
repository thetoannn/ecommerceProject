package com.example.ecommercephone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemDetailResponse {
    private Long id;
    private String productName;
    private String brandProduct;
    private Integer quantity;
    private BigDecimal price;
    private List<OrderItemAttributeRepsonse> attributes;
}
