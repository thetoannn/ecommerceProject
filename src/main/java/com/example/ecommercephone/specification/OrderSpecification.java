package com.example.ecommercephone.specification;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.entity.Order;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("account", JoinType.LEFT);
            }

            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            
            Join<Order, Account> accountJoin = root.join("account", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(root.get("id").as(String.class), pattern));
            predicates.add(cb.like(cb.lower(accountJoin.get("username")), pattern));
            predicates.add(cb.like(cb.lower(accountJoin.get("email")), pattern));

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
