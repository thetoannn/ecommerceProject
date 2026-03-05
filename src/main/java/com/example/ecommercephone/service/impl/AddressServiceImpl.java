package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.CreateAddressRequest;
import com.example.ecommercephone.entity.Address;
import com.example.ecommercephone.repository.AddressRepository;
import com.example.ecommercephone.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public Address createAddress(Long profileId, CreateAddressRequest request) {
        Address address = Address.builder()
                .profileId(profileId)
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .country(request.getCountry() != null ? request.getCountry() : "Việt Nam")
                .build();
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressesByProfileId(Long profileId) {
        return addressRepository.findByProfileId(profileId);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long profileId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getProfileId().equals(profileId)) {
            throw new IllegalStateException("Bạn không có quyền xóa địa chỉ này");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, Long profileId, CreateAddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getProfileId().equals(profileId)) {
            throw new IllegalStateException("Bạn không có quyền chỉnh sửa địa chỉ này");
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());

        return addressRepository.save(address);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }
}
