package com.shoreexcursions.app.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a tour.
 * Validation rules match LLD Section 6.5.
 *
 * @see US-005, US-007, US-027, API-011, API-012
 */
public record TourRequestDto(
        @NotBlank(message = "Port code is required")
        String portCode,

        @NotBlank(message = "Tour name is required")
        @Size(max = 200, message = "Tour name must be at most 200 characters")
        String tourName,

        @Positive(message = "Duration must be a positive number")
        Integer durationMinutes,

        @Size(max = 100, message = "Category must be at most 100 characters")
        String category,

        String description,
        String highlights,
        String bookingConditions,
        Boolean mealIncluded,
        String activityLevel,
        String accessibilityInfo
) {}
