/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.controller;

import com.ubaidsample.h2.dto.response.ApplicationStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/status")
@Tag(name = "ApplicationStatusController", description = "Operations related to Application Status")
public class ApplicationStatusController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Operation(
            summary = "Fetch application status",
            description = "This endpoint retrieves the current status of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping({"", "/"})
    public ApplicationStatusResponse getApplicationStatus() {
        log.info("ApplicationStatusController -> getApplicationStatus() called");
        return new ApplicationStatusResponse(applicationName, "Up and running");
    }
}