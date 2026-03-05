package com.example.ecommercephone.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {

    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String addressLine;

    @Size(max = 100, message = "Thành phố tối đa 100 ký tự")
    private String city;

    @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
    private String country;
}


