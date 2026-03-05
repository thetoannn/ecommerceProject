package com.example.ecommercephone.dto.response;

import com.example.ecommercephone.entity.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminProductResponse {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final Integer stock;
    private final String description;
    private final Integer brandId;
    private final String brandName;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final List<ProductImageResponse> images;
    private final Map<Integer, ProductAttributeResponse> attributes;

    public static AdminProductResponse from(Product product) {
        List<ProductImageResponse> imageResponses = null;
        if (product.getImages() != null) {
            imageResponses = product.getImages().stream()
                .map(img -> ProductImageResponse.builder()
                    .id(img.getId())
                    .imagePath(img.getUrl())
                    .isPrimary(img.getIsPrimary())
                    .build())
                .collect(Collectors.toList());
        }

        Map<Integer, ProductAttributeResponse> attributeMap = null;
        if (product.getProductAttribute() != null) {
            attributeMap = product.getProductAttribute().stream()
                .collect(Collectors.toMap(
                    pa -> pa.getAttribute().getId(),
                    pa -> ProductAttributeResponse.builder()
                        .attributeId(pa.getAttribute().getId())
                        .attributeName(pa.getAttribute().getName())
                        .attributeValueId(pa.getAttributeValue().getId())
                        .attributeValueName(pa.getAttributeValue().getValue())
                        .build()
                ));
        }

        return AdminProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .stock(product.getStock())
            .description(product.getDescription())
            .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
            .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .images(imageResponses)
            .attributes(attributeMap)
            .build();
    }
}
