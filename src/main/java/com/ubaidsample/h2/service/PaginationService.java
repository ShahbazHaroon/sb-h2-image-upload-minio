/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.service;

import com.ubaidsample.h2.dto.common.GenericSpecification;
import com.ubaidsample.h2.dto.request.PageRequestDTO;
import com.ubaidsample.h2.dto.response.PageResponseDTO;
import com.ubaidsample.h2.util.EntityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService<T, R> {

    private final JpaSpecificationExecutor<T> specRepository;
    private final ModelMapper modelMapper;
    private final Class<R> responseClass;
    private final Class<T> entityClass;
    private final String idFieldName;

    public PaginationService(JpaSpecificationExecutor<T> specRepository, ModelMapper modelMapper,
                             Class<T> entityClass, Class<R> responseClass) {
        this.specRepository = specRepository;
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.responseClass = responseClass;
        this.idFieldName = EntityUtil.getIdFieldName(entityClass);
    }

    public PageResponseDTO<R> getPaginatedData(PageRequestDTO pageRequest) {

        // Determine correct sort field
        String sortField = pageRequest.getSortBy();
        // If sortBy is missing or invalid → use auto-detected primary key
        if (sortField == null || sortField.isBlank()) {
            sortField = idFieldName;
        }
        // Validate: check that the field really exists in the entity
        try {
            entityClass.getDeclaredField(sortField);
        } catch (NoSuchFieldException e) {
            sortField = idFieldName;
        }
        Sort sort = pageRequest.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), sort);
        Specification<T> spec = new GenericSpecification<>(pageRequest.getSearch(), pageRequest.getFilters());
        Page<T> entityPage = specRepository.findAll(spec, pageable);
        // Map Entity → ResponseDTO
        List<R> mappedList = entityPage.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, responseClass))
                .toList();
        return new PageResponseDTO<>(
                mappedList,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast()
        );
    }
}