package com.example.ecommercephone.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProductRequest {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    @DecimalMax(value = "1000000000", message = "Giá bán không được vượt quá 1 tỷ")
    @Digits(integer = 9, fraction = 2, message = "Giá bán tối đa 9 chữ số và 2 chữ số thập phân")
    private BigDecimal price;

    @Min(value = 0, message = "Tồn kho phải >= 0")
    @Max(value = 1000000, message = "Tồn kho không vượt quá 1 triệu sản phẩm")
    private Integer stock = 0;

    @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
    private String description;

    @NotNull(message = "Vui lòng chọn thương hiệu")
    private Integer brandId;

    private List<AttributeSelection> attributes;
}
