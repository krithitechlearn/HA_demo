package com.shoreexcursions.app.tour.repository;

import com.shoreexcursions.app.tour.domain.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for TourImage entities.
 *
 * @see US-031
 */
@Repository
public interface TourImageRepository extends JpaRepository<TourImage, UUID> {

    List<TourImage> findByTourIdOrderByDisplayOrderAsc(UUID tourId);

    long countByTourId(UUID tourId);

    void deleteByTourId(UUID tourId);
}
