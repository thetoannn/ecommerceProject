package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.AdminProductRequest;
import com.example.ecommercephone.dto.response.ColorOptionResponse;
import com.example.ecommercephone.dto.response.ProductStatistics;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> findAll();

    Product findById(Long id);

    List<Product> search(String q);

    Page<Product> search(String q, Pageable pageable);

    List<Product> findLatest(int limit);

    List<Product> findTopSelling(int limit);

    Map<String, String> getProductAttributeMap(Product product);

    List<ColorOptionResponse> getColorOptions(Product product);

    List<ProductImage> getProductImages(Product product);

    Product createProduct(AdminProductRequest request, MultipartFile[] images);

    Product updateProduct(Long id, AdminProductRequest request, MultipartFile[] images);

    Page<Product> getProductsAdmin(int page, int size);

    Page<Product> searchProductsAdmin(String query, int page, int size);

    ProductStatistics getProductStatistics();


    void softDeleteProduct(Long id);

    void restoreProduct(Long id);

    void outOfStockProduct(Long id);

    void changeProductStatus(Long id, String status);
}


