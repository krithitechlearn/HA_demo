package com.shoreexcursions.app.tour.domain;

import com.shoreexcursions.app.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a shore excursion tour.
 * Maps to the {@code tour} table.
 *
 * @see US-005, US-006, US-007, US-008
 */
@Entity
@Table(name = "tour")
@Getter
@Setter
public class Tour extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tour_id", nullable = false, updatable = false)
    private UUID tourId;

    @Column(name = "port_code", length = 10, nullable = false)
    private String portCode;

    @Column(name = "tour_name", length = 200, nullable = false)
    private String tourName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "highlights", columnDefinition = "TEXT")
    private String highlights;

    @Column(name = "booking_conditions", columnDefinition = "TEXT")
    private String bookingConditions;

    @Column(name = "meal_included", nullable = false)
    private boolean mealIncluded;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 20)
    private ActivityLevel activityLevel;

    @Column(name = "accessibility_info", columnDefinition = "TEXT")
    private String accessibilityInfo;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @OneToMany(mappedBy = "tourId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<TourImage> images = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tour tour)) return false;
        return Objects.equals(tourId, tour.tourId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tourId);
    }

    @Override
    public String toString() {
        return "Tour{tourId=" + tourId + ", tourName='" + tourName + "', portCode='" + portCode + "'}";
    }
}
