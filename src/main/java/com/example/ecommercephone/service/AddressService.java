package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.CreateAddressRequest;
import com.example.ecommercephone.entity.Address;

import java.util.List;

public interface AddressService {
    Address createAddress(Long profileId, CreateAddressRequest request);
    List<Address> getAddressesByProfileId(Long profileId);
    void deleteAddress(Long addressId, Long profileId);
    Address updateAddress(Long addressId, Long profileId, CreateAddressRequest request);
    Address getAddressById(Long id);
}
