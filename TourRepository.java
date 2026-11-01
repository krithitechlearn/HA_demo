package com.shoreexcursions.app.tour.repository;

import com.shoreexcursions.app.tour.domain.ActivityLevel;
import com.shoreexcursions.app.tour.domain.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Tour entities.
 *
 * @see US-005, US-006, US-007, US-008, US-010, US-015, US-016, US-017
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, UUID> {

    Page<Tour> findByPortCode(String portCode, Pageable pageable);

    Page<Tour> findByCategory(String category, Pageable pageable);

    Page<Tour> findByActivityLevel(ActivityLevel activityLevel, Pageable pageable);

    Page<Tour> findByPortCodeAndCategory(String portCode, String category, Pageable pageable);

    Page<Tour> findByPortCodeAndActivityLevel(String portCode, ActivityLevel activityLevel, Pageable pageable);

    Page<Tour> findByCategoryAndActivityLevel(String category, ActivityLevel activityLevel, Pageable pageable);

    Page<Tour> findByPortCodeAndCategoryAndActivityLevel(String portCode, String category,
                                                          ActivityLevel activityLevel, Pageable pageable);

    long countByPortCode(String portCode);

    @EntityGraph(attributePaths = {"images"})
    @Query("SELECT t FROM Tour t WHERE t.tourId = :tourId")
    Optional<Tour> findByTourIdWithImages(@Param("tourId") UUID tourId);
}
