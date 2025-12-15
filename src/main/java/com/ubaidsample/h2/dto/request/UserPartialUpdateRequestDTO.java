/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPartialUpdateRequestDTO {

    @Size(min = 4, max = 50, message = "User name must be between 4 and 50 characters.")
    @JsonProperty("user_name")
    private String userName;

    @Email(message = "Email address must be valid and follow standard email format.")
    @JsonProperty("email")
    private String email;

    @Size(min = 4, max = 255, message = "Password must be between 4 and 255 characters.")
    @JsonProperty("password")
    private String password;

    @Past(message = "Date of birth must be in the past.")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @Future(message = "Date of leaving must be in the future.")
    @JsonProperty("date_of_leaving")
    private LocalDate dateOfLeaving;

    @Digits(integer = 5, fraction = 0, message = "Postal code must be a valid number with up to 5 digits and no decimals.")
    @Positive(message = "Postal code must be a positive number.")
    @JsonProperty("postal_code")
    private Integer postalCode;

    @JsonProperty("profile_image_object_name")
    private String profileImageObjectName;

    @JsonProperty("profile_image_bucket")
    private String profileImageBucket;
}