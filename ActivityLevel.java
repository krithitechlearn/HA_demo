package com.shoreexcursions.app.tour.domain;

/**
 * Enum representing the physical activity level of a tour.
 * Maps to the {@code activity_level} column in the {@code tour} table.
 *
 * @see LLD Section 5.3
 */
public enum ActivityLevel {
    LOW,
    MODERATE,
    HIGH,
    EXTREME
}
