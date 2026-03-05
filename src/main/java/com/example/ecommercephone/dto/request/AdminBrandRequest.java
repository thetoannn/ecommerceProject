package com.example.ecommercephone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBrandRequest {

    private Integer id;

    @NotBlank(message = "Tên hãng không được để trống")
    @Size(max = 100, message = "Tên hãng tối đa 100 ký tự")
    private String name;
}

