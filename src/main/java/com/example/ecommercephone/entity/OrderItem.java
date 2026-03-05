package com.example.ecommercephone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private Long productId;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name = "price", precision = 20, scale = 2)
    private BigDecimal price;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "brand_product")
    private String brandProduct;

    @Column(name = "product_description")
    private String  productDescription;

    @Column(name = "product_attribute", columnDefinition = "JSON")
    private String productAttribute;
}


