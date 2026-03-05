package com.example.ecommercephone.specification;

import com.example.ecommercephone.entity.Account;
import com.example.ecommercephone.enums.AccountStatus;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {
    public static Specification<Account> filter(String query, String role, String status) {
        return (root, queryCriteria, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (query != null && !query.isEmpty()) {
                String keyword = "%" + query.toLowerCase() + "%";
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), keyword),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), keyword),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("uid")), keyword)
                        )
                );
            }

            if (role != null && !role.isEmpty()) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("role")), role.toLowerCase())
                );
            }

            if (status != null && !status.isEmpty()) {
                AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get("status"), accountStatus)
                );
            }

            return predicates;
        };
    }
}
