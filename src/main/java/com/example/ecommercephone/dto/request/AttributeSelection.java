package com.example.ecommercephone.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeSelection {
    private Integer attributeId;

    @NotNull(message = "Giá trị thuộc tính không được để trống")
    private Integer valueId;
}
