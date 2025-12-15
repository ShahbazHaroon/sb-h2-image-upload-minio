/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.dto.common;

import com.ubaidsample.h2.dto.request.FilterRequestDTO;
import com.ubaidsample.h2.exception.InvalidFilterException;
import com.ubaidsample.h2.util.EntityUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {

    private final String search;
    private final List<FilterRequestDTO> filters;

    public GenericSpecification(String search, List<FilterRequestDTO> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();
        if (filters != null) {
            for (FilterRequestDTO f : filters) {
                Path<?> path = getPath(root, f.getField());
                Class<?> fieldType = path.getJavaType();
                Object typedValue = f.getValue();
                // Convert value(s) to the correct type
                if (typedValue instanceof Collection<?> rawCol) {
                    typedValue = rawCol.stream()
                            .map(v -> EntityUtil.convertValue(fieldType, v))
                            .toList();
                } else {
                    typedValue = EntityUtil.convertValue(fieldType, typedValue);
                }
                String operator = f.getOperator().toLowerCase();
                switch (operator) {
                    case "eq" -> predicates.add(cb.equal(path, typedValue));
                    case "ne" -> predicates.add(cb.notEqual(path, typedValue));
                    case "like" -> {
                        if (!String.class.isAssignableFrom(fieldType)) {
                            throw new InvalidFilterException("LIKE operator only applies to String fields: " + f.getField());
                        }
                        predicates.add(cb.like((Expression<String>) path.as(String.class), "%" + typedValue + "%"));
                    }
                    case "lt", "lte", "gt", "gte" -> {
                        if (!Comparable.class.isAssignableFrom(fieldType)) {
                            throw new InvalidFilterException("Field " + f.getField() + " with operator " + operator + " is not Comparable");
                        }
                        Expression<? extends Comparable> exp = path.as((Class<? extends Comparable>) fieldType);
                        Comparable compValue = (Comparable) typedValue;
                        switch (operator) {
                            case "lt" -> predicates.add(cb.lessThan(exp, compValue));
                            case "lte" -> predicates.add(cb.lessThanOrEqualTo(exp, compValue));
                            case "gt" -> predicates.add(cb.greaterThan(exp, compValue));
                            case "gte" -> predicates.add(cb.greaterThanOrEqualTo(exp, compValue));
                        }
                    }
                    case "in" -> {
                        Collection<?> values = (typedValue instanceof Collection<?> col) ? col : List.of(typedValue);
                        predicates.add(path.in(values));
                    }
                    default -> throw new InvalidFilterException("Unsupported operator: " + operator);
                }
            }
        }
        if (search != null && !search.isBlank()) {
            // Predefined searchable fields (String fields)
            List<String> searchableFields = Arrays.stream(root.getJavaType().getDeclaredFields())
                    .filter(f -> f.getType().equals(String.class))
                    .map(Field::getName)
                    .toList();
            if (!searchableFields.isEmpty()) {
                List<Predicate> searchPreds = searchableFields.stream()
                        .map(fieldName -> (Expression<String>) getPath(root, fieldName).as(String.class))
                        .map(expr -> cb.like(expr, "%" + search + "%"))
                        .toList();
                predicates.add(cb.or(searchPreds.toArray(new Predicate[0])));
            }
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    }

    private Path<?> getPath(Root<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }
}