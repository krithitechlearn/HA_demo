package com.shoreexcursions.app.tour.service;

import com.shoreexcursions.app.tour.dto.TourFilterDto;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for tour management operations.
 *
 * @see US-005, US-006, US-007, US-008, US-010, LLD Section 6.3
 */
public interface TourService {

    TourResponseDto createTour(TourRequestDto dto, String currentUser);

    TourResponseDto getTour(UUID tourId);

    Page<TourSummaryDto> getAllTours(TourFilterDto filter, Pageable pageable);

    Page<TourSummaryDto> getToursByPort(String portCode, Pageable pageable);

    TourResponseDto updateTour(UUID tourId, TourRequestDto dto, String currentUser);

    void deleteTour(UUID tourId, String currentUser);
}
