package com.shoreexcursions.app.tour.dto;

/**
 * Filter parameters for querying tours.
 *
 * @see US-010, US-015, API-009
 */
public record TourFilterDto(
        String portCode,
        String category,
        String activityLevel
) {}
