package com.shoreexcursions.app.tour.service;

import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.exception.BusinessRuleViolationException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.port.repository.PortRepository;
import com.shoreexcursions.app.tour.domain.Tour;
import com.shoreexcursions.app.tour.dto.TourFilterDto;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import com.shoreexcursions.app.tour.mapper.TourMapper;
import com.shoreexcursions.app.tour.repository.TourRepository;
import com.shoreexcursions.app.tour.service.impl.TourServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TourServiceImpl.
 *
 * @see US-005, US-006, US-007, US-008, US-025, US-026
 */
@ExtendWith(MockitoExtension.class)
class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private PortRepository portRepository;
    @Mock
    private TourMapper tourMapper;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private TourServiceImpl tourService;

    private UUID tourId;
    private TourRequestDto requestDto;
    private Tour tour;
    private TourResponseDto responseDto;

    @BeforeEach
    void setUp() {
        tourId = UUID.randomUUID();
        requestDto = new TourRequestDto("MIAMI", "City Tour", 120, "Culture",
                null, null, null, false, "LOW", null);
        tour = new Tour();
        tour.setTourId(tourId);
        tour.setPortCode("MIAMI");
        tour.setTourName("City Tour");
        responseDto = new TourResponseDto(tourId, "MIAMI", "Miami", "City Tour",
                120, "Culture", null, null, null, false, "LOW", null,
                Collections.emptyList(), null, null, "SYSTEM", "SYSTEM", 0);
    }

    @Test
    @DisplayName("US-005: createTour_validPort_returnsCreatedTour")
    void createTour_validPort_returnsCreatedTour() {
        when(portRepository.existsByPortCode("MIAMI")).thenReturn(true);
        when(tourMapper.toEntity(requestDto)).thenReturn(tour);
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toResponseDto(tour)).thenReturn(responseDto);
        when(portRepository.findById("MIAMI")).thenReturn(Optional.empty());

        TourResponseDto result = tourService.createTour(requestDto, "editor");

        assertThat(result.tourId()).isEqualTo(tourId);
        verify(auditService).log(eq("TOUR"), any(), any(), eq("editor"), isNull(), any());
    }

    @Test
    @DisplayName("US-026: createTour_invalidPort_throwsResourceNotFoundException")
    void createTour_invalidPort_throwsResourceNotFoundException() {
        when(portRepository.existsByPortCode("INVALID")).thenReturn(false);
        TourRequestDto invalidDto = new TourRequestDto("INVALID", "Tour", null, null,
                null, null, null, false, null, null);

        assertThatThrownBy(() -> tourService.createTour(invalidDto, "editor"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("INVALID");
    }

    @Test
    @DisplayName("US-006: getTour_existingId_returnsTour")
    void getTour_existingId_returnsTour() {
        when(tourRepository.findByTourIdWithImages(tourId)).thenReturn(Optional.of(tour));
        when(tourMapper.toResponseDto(tour)).thenReturn(responseDto);
        when(portRepository.findById("MIAMI")).thenReturn(Optional.empty());

        TourResponseDto result = tourService.getTour(tourId);

        assertThat(result.tourId()).isEqualTo(tourId);
    }

    @Test
    @DisplayName("US-006: getTour_nonExistingId_throwsResourceNotFoundException")
    void getTour_nonExistingId_throwsResourceNotFoundException() {
        when(tourRepository.findByTourIdWithImages(tourId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.getTour(tourId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("US-007: updateTour_invalidPort_throwsBusinessRuleViolationException")
    void updateTour_invalidPort_throwsBusinessRuleViolationException() {
        when(tourRepository.findByTourIdWithImages(tourId)).thenReturn(Optional.of(tour));
        when(portRepository.existsByPortCode("INVALID")).thenReturn(false);
        TourRequestDto invalidDto = new TourRequestDto("INVALID", "Tour", null, null,
                null, null, null, false, null, null);

        assertThatThrownBy(() -> tourService.updateTour(tourId, invalidDto, "editor"))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("US-008: deleteTour_existingTour_deletesSuccessfully")
    void deleteTour_existingTour_deletesSuccessfully() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(tourMapper.toResponseDto(tour)).thenReturn(responseDto);

        tourService.deleteTour(tourId, "admin");

        verify(tourRepository).delete(tour);
        verify(auditService).log(eq("TOUR"), any(), any(), eq("admin"), any(), isNull());
    }

    @Test
    @DisplayName("US-010: getAllTours_noFilter_returnsAllTours")
    void getAllTours_noFilter_returnsAllTours() {
        Pageable pageable = PageRequest.of(0, 20);
        TourSummaryDto summary = new TourSummaryDto(tourId, "MIAMI", "City Tour", "Culture", "LOW", 120, false);
        when(tourRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(tour)));
        when(tourMapper.toSummaryDto(tour)).thenReturn(summary);

        Page<TourSummaryDto> result = tourService.getAllTours(new TourFilterDto(null, null, null), pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
