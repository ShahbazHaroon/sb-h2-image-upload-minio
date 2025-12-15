/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.util;

import com.ubaidsample.h2.exception.ResourceNotFoundException;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class EntityUtil {

    /**
     * Finds the name of the @Id field for the given entity class.
     */
    public static <T> String getIdFieldName(Class<T> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .map(Field::getName)
                .orElseThrow(() -> new ResourceNotFoundException("Entity " + entityClass.getSimpleName() + " does not have a field annotated with @Id"));
    }

    /**
     * Converts a value (or collection of values) to the target type.
     * Supports single values and collections (lists, sets).
     */
    @SuppressWarnings("unchecked")
    public static <T> Object convertValue(Class<T> targetType, Object value) {

        if (value == null) return null;
        // Handle collections recursively
        if (value instanceof Collection<?> col) {
            return col.stream()
                    .map(v -> convertValue(targetType, v))
                    .toList();
        }
        // Convert single value
        if (targetType.equals(String.class)) {
            return value.toString();
        }
        if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.valueOf(value.toString());
        }
        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.valueOf(value.toString());
        }
        if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return Double.valueOf(value.toString());
        }
        if (targetType.equals(Float.class) || targetType.equals(float.class)) {
            return Float.valueOf(value.toString());
        }
        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return Boolean.valueOf(value.toString());
        }
        if (targetType.equals(LocalDate.class)) {
            return LocalDate.parse(value.toString());
        }
        if (targetType.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(value.toString());
        }
        // For unsupported or custom types, return as-is
        return value;
    }
}