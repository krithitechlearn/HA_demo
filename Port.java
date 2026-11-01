package com.shoreexcursions.app.port.domain;

import com.shoreexcursions.app.common.AuditableEntity;
import com.shoreexcursions.app.tour.domain.Tour;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity representing a shore excursion port/destination.
 * Maps to the {@code port} table.
 *
 * @see US-001, US-002, US-003, US-004
 */
@Entity
@Table(name = "port")
@Getter
@Setter
public class Port extends AuditableEntity {

    @Id
    @Column(name = "port_code", length = 10, nullable = false)
    private String portCode;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "region", length = 100, nullable = false)
    private String region;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "map_link", columnDefinition = "TEXT")
    private String mapLink;

    @Column(name = "site_highlights", columnDefinition = "TEXT")
    private String siteHighlights;

    @Column(name = "fun_facts", columnDefinition = "TEXT")
    private String funFacts;

    @OneToMany(mappedBy = "portCode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<PortImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "portCode", fetch = FetchType.LAZY)
    private List<Tour> tours = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Port port)) return false;
        return Objects.equals(portCode, port.portCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portCode);
    }

    @Override
    public String toString() {
        return "Port{portCode='" + portCode + "', name='" + name + "', region='" + region + "'}";
    }
}
