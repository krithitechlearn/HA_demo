package com.shoreexcursions.app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a user.
 * Validation rules match LLD Section 6.5.
 *
 * @see US-038, API-021, API-022
 */
public record UserRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,

        @NotBlank(message = "Username is required")
        @Size(max = 100, message = "Username must be at most 100 characters")
        String username,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$",
                message = "Password must be at least 12 characters with uppercase, lowercase, digit, and special character"
        )
        String password,

        Boolean enabled
) {}
