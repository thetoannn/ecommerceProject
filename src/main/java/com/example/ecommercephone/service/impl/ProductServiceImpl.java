package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.AdminProductRequest;
import com.example.ecommercephone.dto.request.ProductAttributeRequest;
import com.example.ecommercephone.dto.response.ColorOptionResponse;
import com.example.ecommercephone.dto.response.ProductStatistics;
import com.example.ecommercephone.entity.Brand;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.ProductAttribute;
import com.example.ecommercephone.entity.ProductImage;
import com.example.ecommercephone.enums.ProductColor;
import com.example.ecommercephone.enums.ProductStatus;
import com.example.ecommercephone.repository.BrandRepository;
import com.example.ecommercephone.repository.ProductAttributeRepository;
import com.example.ecommercephone.repository.ProductImageRepository;
import com.example.ecommercephone.repository.ProductRepository;
import com.example.ecommercephone.service.ProductAttributeService;
import com.example.ecommercephone.service.ProductImageService;
import com.example.ecommercephone.service.ProductService;
import com.example.ecommercephone.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final List<String> ADMIN_VISIBLE_STATUSES = Arrays.asList(
            ProductStatus.ACTIVE.name(),
            ProductStatus.INACTIVE.name(),
            ProductStatus.DELETED.name()
    );

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ProductAttributeService productAttributeService;

    @Override
    public List<Product> findAll() {
        return productRepository.findByStatusAndStockGreaterThan(ProductStatus.ACTIVE.name(), 0);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));
    }

    @Override
    public List<Product> search(String q) {
        if (q == null || q.trim().isEmpty()) return findAll();
        return productRepository.findByNameContainingIgnoreCaseAndStatusAndStockGreaterThan(q, ProductStatus.ACTIVE.name(), 0);
    }

    @Override
    public Page<Product> search(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return productRepository.findByStatusAndStockGreaterThan(ProductStatus.ACTIVE.name(), 0, pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseAndStatusAndStockGreaterThan(q, ProductStatus.ACTIVE.name(), 0, pageable);
    }

    @Override
    public List<Product> findLatest(int limit) {
        var page = PageRequest.of(0, Math.max(limit, 1));
        var latest = productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE.name(), page);
        if (latest.isEmpty()) {
            return findAll().stream()
                .sorted((a, b) -> {
                    if (a.getId() == null || b.getId() == null) return 0;
                    return b.getId().compareTo(a.getId());
                })
                .limit(limit)
                .collect(Collectors.toList());
        }
        return latest;
    }

    @Override
    public List<Product> findTopSelling(int limit) {
        var page = PageRequest.of(0, Math.max(limit, 1));
        var top = productRepository.findByStatusOrderByPriceDesc(ProductStatus.ACTIVE.name(), page);
        if (top.isEmpty()) {
            return findAll().stream()
                .sorted((a, b) -> {
                    if (a.getPrice() == null || b.getPrice() == null) return 0;
                    return b.getPrice().compareTo(a.getPrice());
                })
                .limit(limit)
                .collect(Collectors.toList());
        }
        return top;
    }

    @Override
    public Map<String, String> getProductAttributeMap(Product product) {
        if (product == null || product.getProductAttribute() == null) return Map.of();
        return product.getProductAttribute().stream()
            .collect(Collectors.toMap(
                pa -> pa.getAttribute().getName(),
                pa -> pa.getAttributeValue().getValue()
            ));
    }

    @Override
    public List<ColorOptionResponse> getColorOptions(Product product) {
        if (product == null || product.getProductAttribute() == null) return List.of();

        return product.getProductAttribute().stream()
                .filter(pa -> "Color".equalsIgnoreCase(pa.getAttribute().getName()))
                .map(pa -> pa.getAttributeValue().getValue())
                .distinct()
                .map(colorValue -> new ColorOptionResponse(
                        colorValue,
                        ProductColor.resolveHex(colorValue),
                        null
                ))
                .toList();
    }

    @Override
    public List<ProductImage> getProductImages(Product product) {
        if (product == null || product.getImages() == null) {
            return List.of();
        }
        return product.getImages();
    }

    @Override
    @Transactional
    public Product createProduct(AdminProductRequest request, MultipartFile[] images) {
        productImageService.validateImages(images, null, false);

        Integer brandId = request.getBrandId();

        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Thương hiệu không hợp lệ"));

        Product product = Product.builder()
            .name(request.getName())
            .price(request.getPrice())
            .stock(request.getStock())
            .description(request.getDescription())
            .brand(brand)
            .status(ProductStatus.ACTIVE.name())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        productRepository.save(product);

        if (images != null && images.length > 0) {
            productImageService.saveProductImages(product, images);
        }

        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            productAttributeService.addNewAttributes(product, new ProductAttributeRequest(request.getAttributes()));
        }

        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, AdminProductRequest request, MultipartFile[] images) {
        if (id == null) {
            throw new IllegalArgumentException("Thiếu mã sản phẩm để cập nhật");
        }

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));

        productImageService.validateImages(images, id, true);

        Integer brandId = request.getBrandId();

        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Thương hiệu không hợp lệ"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setBrand(brand);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);

        if (images != null && images.length > 0) {
            productImageService.saveProductImages(product, images);
        }

        if (request.getAttributes() != null) {
            ProductAttributeRequest attrRequest = new ProductAttributeRequest(request.getAttributes());
            productAttributeService.updateProductAttributes(product, attrRequest);
        }

        return product;
    }

    @Override
    public Page<Product> getProductsAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByStatusIn(ADMIN_VISIBLE_STATUSES, pageable);
    }

    @Override
    public Page<Product> searchProductsAdmin(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findAll(ProductSpecification.searchByQuery(query, ADMIN_VISIBLE_STATUSES),pageable);
    }

    @Override
    public ProductStatistics getProductStatistics() {
        List<Object[]> results = productRepository.getProductStatistics(ADMIN_VISIBLE_STATUSES);
        
        if (results == null || results.isEmpty() || results.get(0) == null) {
            return ProductStatistics.builder()
                .totalProducts(0L)
                .outOfStock(0L)
                .lowStock(0L)
                .inventoryValue(BigDecimal.ZERO)
                .build();
        }

        Object[] stats = results.get(0);
        long totalProducts = stats[0] != null ? ((Number) stats[0]).longValue() : 0L;
        long outOfStock = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;
        long lowStock = stats[2] != null ? ((Number) stats[2]).longValue() : 0L;
        BigDecimal inventoryValue = stats[3] != null ? (BigDecimal) stats[3] : BigDecimal.ZERO;

        return ProductStatistics.builder()
            .totalProducts(totalProducts)
            .outOfStock(outOfStock)
            .lowStock(lowStock)
            .inventoryValue(inventoryValue)
            .build();
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));

        product.setStatus(ProductStatus.DELETED.name());
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));
        product.setStatus(ProductStatus.ACTIVE.name());
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void outOfStockProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));
        product.setStatus(ProductStatus.OUT_OF_STOCK.name());
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void changeProductStatus(Long id, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));

        ProductStatus newStatus = ProductStatus.fromString(status);
        product.setStatus(newStatus.name());
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }
}

