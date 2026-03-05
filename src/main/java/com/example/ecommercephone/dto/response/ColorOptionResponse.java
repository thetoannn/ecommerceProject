package com.example.ecommercephone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ColorOptionResponse {
    private final String colorName;
    private final String colorHex;
    private final String imagePath;
}
