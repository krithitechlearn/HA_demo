package com.shoreexcursions.app.port.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response DTO for a full port record including images.
 *
 * @see US-002, US-012, API-005, API-006, API-007
 */
public record PortResponseDto(
        String portCode,
        String name,
        String region,
        String description,
        String mapLink,
        String siteHighlights,
        String funFacts,
        List<PortImageDto> images,
        long tourCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String createdBy,
        String updatedBy
) {}
