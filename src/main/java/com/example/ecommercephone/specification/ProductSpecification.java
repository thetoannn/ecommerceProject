package com.example.ecommercephone.specification;

import com.example.ecommercephone.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {
    public static Specification<Product> searchByQuery(String query, List<String> status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<Product, ProductAttribute> productAttributeJoin = root.join("productAttribute", JoinType.LEFT);
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.LEFT);
            Join<ProductAttribute, Attribute>  attributeJoin = productAttributeJoin.join("attribute", JoinType.LEFT);
            Join<ProductAttribute, AttributeValue>  attributeValueJoin = productAttributeJoin.join("attributeValue", JoinType.LEFT);

            String patern = "%" + query.trim().toLowerCase() + "%";

            Predicate statusPredicate = root.get("status").in(status);
            Predicate namePredicte = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), patern);
            Predicate brandPredicate = criteriaBuilder.like(criteriaBuilder.lower(brandJoin.get("name")), patern);
            Predicate attributePredicate = criteriaBuilder.like(criteriaBuilder.lower(attributeJoin.get("name")), patern);
            Predicate attributeValuePredicate = criteriaBuilder.like(criteriaBuilder.lower(attributeValueJoin.get("value")), patern);

            return criteriaBuilder.and(
                    statusPredicate,
                    criteriaBuilder.or(namePredicte, brandPredicate, attributePredicate, attributeValuePredicate)
            );
        };
    }
}
