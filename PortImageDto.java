package com.shoreexcursions.app.port.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for a port image record.
 *
 * @see US-031
 */
public record PortImageDto(
        UUID id,
        String portCode,
        String imageUrl,
        int displayOrder,
        OffsetDateTime createdAt
) {}
