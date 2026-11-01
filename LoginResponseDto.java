package com.shoreexcursions.app.auth.dto;

import java.util.List;

/**
 * Response DTO for successful login.
 *
 * @see US-037, API-001
 */
public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        List<String> roles
) {}
