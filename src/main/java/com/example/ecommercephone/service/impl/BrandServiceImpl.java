package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.AdminBrandRequest;
import com.example.ecommercephone.entity.Brand;
import com.example.ecommercephone.repository.BrandRepository;
import com.example.ecommercephone.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<Brand> findAllBrands(Sort sort) {
        return brandRepository.findAll(sort);
    }

    @Override
    public Page<Brand> searchBrands(String query, int page, int size) {
        List<Brand> all = brandRepository.findAll(Sort.by("name"));

        List<Brand> filtered = all;
        if (StringUtils.hasText(query)) {
            String keyword = query.trim().toLowerCase(Locale.ROOT);
            filtered = all.stream()
                .filter(brand -> brand.getName() != null &&
                        brand.getName().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        }

        int totalItems = filtered.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalItems);
        List<Brand> pagedBrands = start < totalItems ? filtered.subList(start, end) : List.of();

        return new PageImpl<>(pagedBrands, PageRequest.of(page - 1, size), totalItems);
    }

    @Override
    public long countAll() {
        return brandRepository.count();
    }

    @Override
    public boolean existsByName(String name) {
        return brandRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Brand createBrand(AdminBrandRequest request) {
        Brand brand = Brand.builder()
            .name(request.getName().trim())
            .build();
        return brandRepository.save(brand);
    }
}
