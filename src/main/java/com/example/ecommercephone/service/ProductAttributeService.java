package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.ProductAttributeRequest;
import com.example.ecommercephone.entity.Product;

public interface ProductAttributeService {
    void updateProductAttributes(Product product, ProductAttributeRequest request);
    void addNewAttributes(Product product, ProductAttributeRequest request);
}
