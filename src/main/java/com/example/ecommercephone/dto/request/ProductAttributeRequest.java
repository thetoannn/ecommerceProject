package com.example.ecommercephone.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeRequest {
    @NotNull(message = "Danh sách thuộc tính không được để trống")
    private List<AttributeSelection> attributes;
}
