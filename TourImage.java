package com.shoreexcursions.app.tour.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing an image associated with a tour.
 * Maps to the {@code tour_image} table.
 *
 * @see US-031
 */
@Entity
@Table(name = "tour_image")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class TourImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tour_id", nullable = false, updatable = false)
    private UUID tourId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TourImage ti)) return false;
        return Objects.equals(id, ti.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TourImage{id=" + id + ", tourId=" + tourId + "'}";
    }
}
