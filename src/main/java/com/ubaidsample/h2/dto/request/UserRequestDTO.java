/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Idempotency key is required and cannot be blank.")
    @Size(min = 4, max = 50, message = "Idempotency key must be between 4 and 50 characters.")
    @JsonProperty("idempotency_key")
    private String idempotencyKey;

    @NotBlank(message = "User name is required and cannot be blank.")
    @Size(min = 4, max = 50, message = "User name must be between 4 and 50 characters.")
    @JsonProperty("user_name")
    private String userName;

    @NotBlank(message = "Email address is required and cannot be blank.")
    @Email(message = "Email address must be valid and follow standard email format.")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required and cannot be blank.")
    @Size(min = 4, max = 255, message = "Password must be between 4 and 255 characters.")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Date of birth is required and cannot be null.")
    @Past(message = "Date of birth must be in the past.")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of leaving is required and cannot be null.")
    @Future(message = "Date of leaving must be in the future.")
    @JsonProperty("date_of_leaving")
    private LocalDate dateOfLeaving;

    @NotNull(message = "Postal code is required and cannot be null.")
    @Digits(integer = 5, fraction = 0, message = "Postal code must be a valid number with up to 5 digits and no decimals.")
    @Positive(message = "Postal code must be a positive number.")
    @JsonProperty("postal_code")
    private Integer postalCode;

    @JsonProperty("profile_image_object_name")
    private String profileImageObjectName;

    @JsonProperty("profile_image_bucket")
    private String profileImageBucket;
}