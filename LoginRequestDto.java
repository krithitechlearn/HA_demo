package com.shoreexcursions.app.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user login.
 *
 * @see US-037, API-001
 */
public record LoginRequestDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}
