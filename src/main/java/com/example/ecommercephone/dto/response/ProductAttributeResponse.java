package com.example.ecommercephone.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductAttributeResponse {
    private final Integer attributeId;
    private final String attributeName;
    private final Integer attributeValueId;
    private final String attributeValueName;
}
