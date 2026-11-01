package com.shoreexcursions.app.tour.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a full tour record including images.
 *
 * @see US-006, US-017, API-010, API-011, API-012
 */
public record TourResponseDto(
        UUID tourId,
        String portCode,
        String portName,
        String tourName,
        Integer durationMinutes,
        String category,
        String description,
        String highlights,
        String bookingConditions,
        Boolean mealIncluded,
        String activityLevel,
        String accessibilityInfo,
        List<TourImageDto> images,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String createdBy,
        String updatedBy,
        int version
) {}
