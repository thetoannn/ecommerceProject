package com.example.ecommercephone.service;

import com.example.ecommercephone.dto.request.AdminBrandRequest;
import com.example.ecommercephone.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BrandService {

    List<Brand> findAllBrands(Sort sort);

    Page<Brand> searchBrands(String query, int page, int size);

    long countAll();

    boolean existsByName(String name);

    Brand createBrand(AdminBrandRequest request);
}
