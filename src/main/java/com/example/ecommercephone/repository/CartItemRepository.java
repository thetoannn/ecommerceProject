package com.example.ecommercephone.repository;

import com.example.ecommercephone.entity.CartItem;
import com.example.ecommercephone.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {"product", "product.images"})
    List<CartItem> findByAccountUidOrderByAddedAtDesc(@Param("accountUid") String accountUid);

    @EntityGraph(attributePaths = {"product", "product.brand", "product.productAttribute", "product.productAttribute.attribute", "product.productAttribute.attributeValue"})
    List<CartItem> findFullByAccountUid(@Param("accountUid") String accountUid);

    Optional<CartItem> findByAccountUidAndProduct(String accountUid, Product product);

    @Query("SELECT c.quantity FROM CartItem c WHERE c.accountUid = :accountUid AND c.product.id = :productId")
    Optional<Integer> findQuantityByAccountUidAndProductId(String accountUid, Long productId);

    void deleteByAccountUidAndProduct(String accountUid, Product product);

    void deleteByAccountUid(String accountUid);
}


