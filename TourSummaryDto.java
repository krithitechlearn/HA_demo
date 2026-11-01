package com.shoreexcursions.app.tour.dto;

import java.util.UUID;

/**
 * Summary DTO for tour list views.
 *
 * @see US-010, US-015, API-009
 */
public record TourSummaryDto(
        UUID tourId,
        String portCode,
        String tourName,
        String category,
        String activityLevel,
        Integer durationMinutes,
        boolean mealIncluded
) {}
