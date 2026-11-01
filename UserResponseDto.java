package com.shoreexcursions.app.user.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for a user record. Never includes password hash.
 *
 * @see US-038, API-019, API-020, API-021, API-022
 */
public record UserResponseDto(
        UUID userId,
        String email,
        String username,
        boolean enabled,
        Set<String> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
