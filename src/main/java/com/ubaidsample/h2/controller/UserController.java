/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.controller;

import com.ubaidsample.h2.dto.request.PageRequestDTO;
import com.ubaidsample.h2.dto.request.UserPartialUpdateRequestDTO;
import com.ubaidsample.h2.dto.request.UserRequestDTO;
import com.ubaidsample.h2.dto.response.PageResponseDTO;
import com.ubaidsample.h2.dto.response.UserResponseDTO;
import com.ubaidsample.h2.entity.User;
import com.ubaidsample.h2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(
            summary = "Create new resource",
            description = "Creates a new resource with the provided information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "409", description = "Resource already exists")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> save(
            @Parameter(description = "Resource data to create")
            @Valid @RequestBody UserRequestDTO request) {
        log.info("UserController -> save() called");
        var response = service.save(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getUserId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Upload image",
            description = "Upload image path"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Upload image successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadProfileImage(
            @Parameter(description = "ID of the resource to upload image")
            @PathVariable(value = "id") @Positive Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        var presignedUrl = service.uploadProfileImage(id, file);
        return ResponseEntity.ok(Map.of("image_url", presignedUrl));
    }

    @Operation(
            summary = "Get image",
            description = "Retrieve presigned GET URL for image"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image path retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<?> getProfileImage(
            @Parameter(description = "ID of the resource to get the image")
            @PathVariable(value = "id") @Positive Long id
    ) throws Exception {
        var response = service.findById(id);
        if (response.getProfileImageObjectName() == null) {
            return ResponseEntity.notFound().build();
        }
        var presignedUrl = service.generateDownloadUrl(response.getProfileImageObjectName());
        return ResponseEntity.ok(Map.of("image_url", presignedUrl));
    }

    @Operation(
            summary = "Get all resources",
            description = "Retrieves a list of all resources"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        log.info("UserController -> findAll() called");
        var response = service.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get resource by ID",
            description = "Retrieves a resource by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @Parameter(description = "ID of the resource to retrieve")
            @PathVariable(value = "id") @Positive Long id) {
        log.info("UserController -> findById() called with ID: {}", id);
        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update resource by ID",
            description = "Updates a resource entirely with the provided information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid resource data provided"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @Parameter(description = "ID of the resource to update")
            @PathVariable(value = "id") @Positive Long id,
            @Parameter(description = "Updated resource data")
            @Valid @RequestBody UserRequestDTO request) {
        log.info("UserController -> update() called with ID: {}", id);
        var response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Partially update resource by ID",
            description = "Updates specific fields of a resource"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource partially updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid resource data provided"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> partialUpdate(
            @Parameter(description = "ID of the resource to partially update")
            @PathVariable(value = "id") @Positive Long id,
            @Parameter(description = "Fields to update")
            @RequestBody UserPartialUpdateRequestDTO updates) {
        log.info("UserController -> partialUpdate() called with ID: {}", id);
        var response = service.partialUpdate(id, updates);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete resource by ID",
            description = "Performs a soft delete on a resource by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID of the resource to delete")
            @PathVariable(value = "id") @Positive Long id) {
        log.info("UserController -> deactivate() called with ID: {}", id);
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restore deleted resource by ID",
            description = "Restores a previously soft-deleted resource"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource restored successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found or not deleted")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @Parameter(description = "ID of the resource to restore")
            @PathVariable(value = "id") @Positive Long id) {
        log.info("UserController -> activate() called with ID: {}", id);
        service.activate(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Search with pagination",
            description = "Performs a paginated search on the provided criteria"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping("/search")
    public PageResponseDTO<UserResponseDTO> search(
            @Parameter(description = "Pagination and filter criteria")
            @RequestBody PageRequestDTO pageRequest) {
        log.info("UserController -> search() called");
        return service.search(pageRequest);
    }
}