package com.example.ecommercephone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "image_path", length = 255, nullable = false)
    private String imagePath;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Transient
    public String getUrl() {
        if (imagePath == null) return null;
        return imagePath.replace("\\", "/").replace("D:", "");
    }
}

