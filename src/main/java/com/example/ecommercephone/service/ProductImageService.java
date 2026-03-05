package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    void saveProductImages(Product product, MultipartFile[] images);

    void deleteProductImage(Long imageId);

    long countProductImages(Long productId);

    void validateImages(MultipartFile[] images, Long productId, boolean isUpdate);
}
