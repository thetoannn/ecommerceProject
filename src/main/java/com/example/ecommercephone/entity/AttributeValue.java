package com.example.ecommercephone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @Column(length = 100)
    private String value;
}

