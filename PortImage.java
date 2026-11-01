package com.shoreexcursions.app.port.domain;

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
 * JPA entity representing an image associated with a port.
 * Maps to the {@code port_image} table.
 *
 * @see US-031
 */
@Entity
@Table(name = "port_image")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PortImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "port_code", length = 10, nullable = false, updatable = false)
    private String portCode;

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
        if (!(o instanceof PortImage pi)) return false;
        return Objects.equals(id, pi.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PortImage{id=" + id + ", portCode='" + portCode + "'}";
    }
}
