package com.shoreexcursions.app.port.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a port.
 * Validation rules match LLD Section 6.5.
 *
 * @see US-001, US-003, US-027, API-006, API-007
 */
public record PortRequestDto(
        @NotBlank(message = "Port code is required")
        @Size(max = 10, message = "Port code must be at most 10 characters")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "Port code must be alphanumeric uppercase, max 10 chars")
        String portCode,

        @NotBlank(message = "Port name is required")
        @Size(max = 100, message = "Port name must be at most 100 characters")
        String name,

        @NotBlank(message = "Region is required")
        @Size(max = 100, message = "Region must be at most 100 characters")
        String region,

        String description,
        String mapLink,
        String siteHighlights,
        String funFacts
) {}
