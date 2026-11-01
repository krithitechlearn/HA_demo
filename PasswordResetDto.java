package com.shoreexcursions.app.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for confirming a password reset.
 *
 * @see US-046, API-003
 */
public record PasswordResetDto(
        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$",
                message = "Password must be at least 12 characters with uppercase, lowercase, digit, and special character"
        )
        String newPassword
) {}
