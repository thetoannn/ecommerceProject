package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.ProductImage;
import com.example.ecommercephone.repository.ProductImageRepository;
import com.example.ecommercephone.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    private static final String UPLOAD_DIR = "D:/uploads/products";

    @Override
    public void saveProductImages(Product product, MultipartFile[] images) {
        if (images == null || images.length == 0) return;

        Path productPath = Paths.get(UPLOAD_DIR, String.valueOf(product.getId()));
        ensureDirectoryExists(productPath);

        long existingCount = countProductImages(product.getId());
        boolean hasPrimary = existingCount > 0 && productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(product.getId())
                .stream().anyMatch(img -> Boolean.TRUE.equals(img.getIsPrimary()));

        for (int i = 0; i < images.length; i++) {
            MultipartFile file = images[i];
            try {
                String fileName = generateFileName(file, i);
                Path targetPath = productPath.resolve(fileName);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                String absolutePath = targetPath.toAbsolutePath().toString();
                
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imagePath(absolutePath)
                        .isPrimary(!hasPrimary && i == 0)
                        .build();

                productImageRepository.save(image);
            } catch (IOException e) {
            }
        }
    }

    private void ensureDirectoryExists(Path path) {
        File dir = path.toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Không thể tạo thư mục lưu ảnh");
        }
    }

    private String generateFileName(MultipartFile file, int index) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot);
        }
        return "img_" + System.currentTimeMillis() + "_" + index + extension;
    }

    @Override
    public void deleteProductImage(Long imageId) {
        productImageRepository.findById(imageId).ifPresent(image -> {
            try {
                String imagePath = image.getImagePath();
                if (imagePath != null) {
                    Files.deleteIfExists(Paths.get(imagePath));
                }
                productImageRepository.delete(image);
            } catch (IOException e) {
            }
        });
    }

    @Override
    public long countProductImages(Long productId) {
        return productId == null ? 0 : productImageRepository.countByProductId(productId);
    }

    @Override
    public void validateImages(MultipartFile[] images, Long productId, boolean isUpdate) {
        int validImageCount = 0;
        if (images != null) {
            for (MultipartFile file : images) {
                if (file != null && !file.isEmpty()) {
                    String contentType = file.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        validImageCount++;
                    }
                }
            }
        }

        long totalCount = (isUpdate && productId != null ? countProductImages(productId) : 0) + validImageCount;

        if (totalCount == 0) {
            throw new IllegalArgumentException(isUpdate ? "Sản phẩm phải có ít nhất 1 ảnh." : "Vui lòng chọn ít nhất 1 ảnh");
        }
        if (totalCount > 6) {
            throw new IllegalArgumentException(String.format("Tổng số ảnh không được vượt quá 6 (hiện có %d ảnh)", totalCount));
        }
    }
}

