package com.shoreexcursions.app.tour.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for a tour image record.
 *
 * @see US-031
 */
public record TourImageDto(
        UUID id,
        UUID tourId,
        String imageUrl,
        int displayOrder,
        OffsetDateTime createdAt
) {}
