package com.example.ecommercephone.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ProductImageResponse {
    private final Long id;
    private final String imagePath;
    private final Boolean isPrimary;
    private final Instant createdAt;
}
