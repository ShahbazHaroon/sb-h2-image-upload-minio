/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.service;

import com.ubaidsample.h2.dto.request.PageRequestDTO;
import com.ubaidsample.h2.dto.request.UserPartialUpdateRequestDTO;
import com.ubaidsample.h2.dto.request.UserRequestDTO;
import com.ubaidsample.h2.dto.response.PageResponseDTO;
import com.ubaidsample.h2.dto.response.UserResponseDTO;
import com.ubaidsample.h2.entity.User;
import com.ubaidsample.h2.exception.InvalidFileTypeException;
import com.ubaidsample.h2.exception.MinioOperationException;
import com.ubaidsample.h2.exception.ResourceAlreadyExistsException;
import com.ubaidsample.h2.exception.ResourceNotFoundException;
import com.ubaidsample.h2.repository.UserRepository;
import com.ubaidsample.h2.util.MapperUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final MinioClient minioClient;

    private final UserRepository repository;

    private final ModelMapper modelMapper;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${app.upload.allowed-types}")
    private String allowedTypes;

    @Value("${minio.presigned-url-expiry}")
    private int profileImageUrlExpiry;

    @Transactional
    public UserResponseDTO save(UserRequestDTO request) {
        log.info("UserService -> save() called with idempotencyKey={}", request.getIdempotencyKey());
        // Check if this idempotency key was already processed
        return findByIdempotencyKey(request);

    }

    private UserResponseDTO findByIdempotencyKey(UserRequestDTO request) {
        return repository.findByIdempotencyKey(request.getIdempotencyKey())
                .map(user -> {
                    log.info("Returning existing user for idempotencyKey={}", request.getIdempotencyKey());
                    return modelMapper.map(user, UserResponseDTO.class);
                })
                .orElseGet(() -> saveNewUser(request));
    }

    private UserResponseDTO saveNewUser(UserRequestDTO request) {
        // Convert the DTO to the entity
        User entity = modelMapper.map(request, User.class);
        try {
            // Save the new data
            User response = repository.saveAndFlush(entity);
            // Convert the entity to the DTO
            return modelMapper.map(response, UserResponseDTO.class);
        } catch (DataIntegrityViolationException ex) {
            return handleConstraintViolation(request, ex);
        }
    }

    private UserResponseDTO handleConstraintViolation(UserRequestDTO request, DataIntegrityViolationException ex) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        String constraintName = null;
        if (rootCause instanceof org.hibernate.exception.ConstraintViolationException hibernateCve) {
            constraintName = hibernateCve.getConstraintName();

            /* else if (rootCause instanceof java.sql.SQLIntegrityConstraintViolationException sqlCve) {
                // For MySQL: parse SQL message or vendor error code
                constraintName = extractConstraintNameFromMessage(sqlCve.getMessage());

            } else if (rootCause instanceof org.postgresql.util.PSQLException pgEx) {
                // For Postgres: use ServerErrorMessage for constraint
                if (pgEx.getServerErrorMessage() != null) {
                    constraintName = pgEx.getServerErrorMessage().getConstraint();
                }

            } else if (rootCause instanceof oracle.jdbc.OracleDatabaseException oracleEx) {
                // For Oracle: parse error code or message
                constraintName = extractOracleConstraint(oracleEx);
            }*/

            if (constraintName != null) {
                throw ex;
            }
            return switch (constraintName) {
                case "uk_user_email" -> throw new ResourceAlreadyExistsException("User already exists with email: " + request.getEmail());
                case "uk_user_username" -> throw new ResourceAlreadyExistsException("User already exists with username: " + request.getUserName());
                case "uk_user_idempotency_key" -> repository.findByIdempotencyKey(request.getIdempotencyKey())
                        .map(user -> {
                            log.warn("Concurrent idempotent request detected; returning original result for key={}", request.getIdempotencyKey());
                            return modelMapper.map(user, UserResponseDTO.class);
                        })
                        .orElseThrow(() -> ex);
                default -> throw ex;
            };
        }
        throw ex;
    }

    public String uploadProfileImage(Long id, MultipartFile file) throws IOException, Exception {
        // Validate MIME type
        List<String> allowed = List.of(allowedTypes.split(","));
        if (!allowed.contains(file.getContentType())) {
            throw new InvalidFileTypeException("Only JPEG and PNG files are allowed");
        }
        // Validate extension (case-insensitive)
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
            throw new InvalidFileTypeException("File extension must be .jpg, .jpeg, or .png");
        }
        // Fetch existing
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nothing found in the database with id " + id));
        // Delete old image if exists
        if (entity.getProfileImageObjectName() != null) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(entity.getProfileImageBucket())
                                .object(entity.getProfileImageObjectName())
                                .build()
                );
            } catch (Exception ex) {
                // Log warning but continue
                log.warn("Warning: Could not delete old image: {}", ex.getMessage());
            }
        }
        // Generate object name
        String objectName = "user-" + id + "-" + UUID.randomUUID() + "-" + originalName;
        // Upload to MinIO (outside @Transactional)
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        // Persist objectName + bucket in DB transactionally
        saveImageObjectName(entity, objectName);
        // Return presigned GET URL immediately
        return generateDownloadUrl(objectName);
    }

    @Transactional
    protected void saveImageObjectName(User entity, String objectName) {
        entity.setProfileImageObjectName(objectName);
        entity.setProfileImageBucket(bucket);
        repository.save(entity);
    }

    // Generate presigned GET URL dynamically
    public String generateDownloadUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(profileImageUrlExpiry)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioOperationException("Failed to generate download URL for: " + objectName, e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        log.info("UserService -> findAll() called");
        // Fetch existing
        List<User> entity = repository.findAll();
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Nothing found in the database");
        }
        return entity.stream()
                .map(user -> {
                    // Convert the entity to the DTO
                    return MapperUtil.map(user, UserResponseDTO.class);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        log.info("UserService -> findById() called");
        // Fetch existing
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nothing found in the database with id " + id));
        // Convert the entity to the DTO
        return modelMapper.map(entity, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO request) {
        log.info("UserService -> update() called");
        // Fetch existing
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nothing found in the database with id " + id));
        // Update and map all fields except password
        modelMapper.map(request, entity);
        // Update password only if provided
        if (StringUtils.hasText(request.getPassword())) {
            entity.setPassword(request.getPassword());
        }
        // Save the entity and convert it to the DTO
        return modelMapper.map(repository.save(entity), UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO partialUpdate(Long id, UserPartialUpdateRequestDTO updates) {
        log.info("UserService -> partialUpdate() called");
        // Fetch existing
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nothing found in the database with id " + id));
        // Apply updates only if they are present
        Optional.ofNullable(updates.getUserName()).ifPresent(entity::setUserName);
        Optional.ofNullable(updates.getEmail()).ifPresent(entity::setEmail);
        // Update password only if provided and must not be blank
        Optional.ofNullable(updates.getPassword())
                .filter(StringUtils::hasText)
                .ifPresent(entity::setPassword);
        Optional.ofNullable(updates.getDateOfBirth()).ifPresent(entity::setDateOfBirth);
        Optional.ofNullable(updates.getDateOfLeaving()).ifPresent(entity::setDateOfLeaving);
        Optional.ofNullable(updates.getPostalCode()).ifPresent(entity::setPostalCode);
        // Save the entity and convert it to the DTO
        return modelMapper.map(repository.save(entity), UserResponseDTO.class);
    }

    @Transactional
    public void deactivate(Long userId) {
        log.info("UserService -> deactivate() called");
        // Fetch existing
        repository.findById(userId).ifPresent(user -> {
            user.getAuditHistoryDTO().setDeleted(true);
            user.getAuditHistoryDTO().setDeletedDate(LocalDateTime.now());
            repository.save(user);
        });
    }

    @Transactional
    public void activate(Long userId) {
        log.info("UserService -> activate() called");
        // Fetch existing
        repository.findById(userId).ifPresent(user -> {
            user.getAuditHistoryDTO().setDeleted(false);
            user.getAuditHistoryDTO().setDeletedDate(null);
            repository.save(user);
        });
    }

    public PageResponseDTO<UserResponseDTO> search(PageRequestDTO pageRequest) {
        log.info("UserService -> search() called");
        PaginationService<User, UserResponseDTO> paginationService =
                new PaginationService<>(repository, modelMapper, User.class, UserResponseDTO.class);
        return paginationService.getPaginatedData(pageRequest);
    }

    @Transactional
    public void delete(Long id) {
        log.info("UserService -> delete() called");
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Nothing found in the database with id " + id);
        }
        repository.deleteById(id);
    }
}