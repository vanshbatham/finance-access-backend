package com.zorvyn.finance.specifications;

import com.zorvyn.finance.entities.Category;
import com.zorvyn.finance.entities.FinancialRecord;
import com.zorvyn.finance.entities.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialRecordSpecification {

    public static Specification<FinancialRecord> filterRecords(
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type,
            Category category) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // if a start date is provided, add: WHERE transactionDate >= startDate
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }

            // if an end date is provided, add: AND transactionDate <= endDate
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            // if a type is provided, add: AND type = type
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            // if a category is provided, add: AND category = category
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            // combine all predicates with AND, and return the final predicate
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}