package com.shoreexcursions.app.tour.service.impl;

import com.shoreexcursions.app.audit.AuditAction;
import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.exception.BusinessRuleViolationException;
import com.shoreexcursions.app.exception.DuplicateResourceException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.port.repository.PortRepository;
import com.shoreexcursions.app.tour.domain.ActivityLevel;
import com.shoreexcursions.app.tour.domain.Tour;
import com.shoreexcursions.app.tour.dto.TourFilterDto;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import com.shoreexcursions.app.tour.mapper.TourMapper;
import com.shoreexcursions.app.tour.repository.TourRepository;
import com.shoreexcursions.app.tour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Service implementation for tour management.
 * Enforces all tour business rules per LLD Section 6.3.
 *
 * @see US-005, US-006, US-007, US-008, US-010, US-025, US-026
 */
@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private static final Logger log = LoggerFactory.getLogger(TourServiceImpl.class);

    private final TourRepository tourRepository;
    private final PortRepository portRepository;
    private final TourMapper tourMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public TourResponseDto createTour(TourRequestDto dto, String currentUser) {
        if (!portRepository.existsByPortCode(dto.portCode())) {
            throw new ResourceNotFoundException("Port", dto.portCode());
        }
        Tour tour = tourMapper.toEntity(dto);
        Tour saved = tourRepository.save(tour);
        log.info("Tour created: tourId={} portCode={} by userId-ref={}",
                saved.getTourId(), saved.getPortCode(), currentUser);
        auditService.log("TOUR", saved.getTourId().toString(), AuditAction.CREATE, currentUser, null, dto);
        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponseDto getTour(UUID tourId) {
        Tour tour = tourRepository.findByTourIdWithImages(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
        return buildResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TourSummaryDto> getAllTours(TourFilterDto filter, Pageable pageable) {
        boolean hasPort = filter != null && StringUtils.hasText(filter.portCode());
        boolean hasCat = filter != null && StringUtils.hasText(filter.category());
        boolean hasLevel = filter != null && StringUtils.hasText(filter.activityLevel());

        if (hasPort && hasCat && hasLevel) {
            return tourRepository.findByPortCodeAndCategoryAndActivityLevel(
                    filter.portCode(), filter.category(),
                    ActivityLevel.valueOf(filter.activityLevel().toUpperCase()), pageable)
                    .map(tourMapper::toSummaryDto);
        } else if (hasPort && hasCat) {
            return tourRepository.findByPortCodeAndCategory(filter.portCode(), filter.category(), pageable)
                    .map(tourMapper::toSummaryDto);
        } else if (hasPort && hasLevel) {
            return tourRepository.findByPortCodeAndActivityLevel(filter.portCode(),
                    ActivityLevel.valueOf(filter.activityLevel().toUpperCase()), pageable)
                    .map(tourMapper::toSummaryDto);
        } else if (hasCat && hasLevel) {
            return tourRepository.findByCategoryAndActivityLevel(filter.category(),
                    ActivityLevel.valueOf(filter.activityLevel().toUpperCase()), pageable)
                    .map(tourMapper::toSummaryDto);
        } else if (hasPort) {
            return tourRepository.findByPortCode(filter.portCode(), pageable).map(tourMapper::toSummaryDto);
        } else if (hasCat) {
            return tourRepository.findByCategory(filter.category(), pageable).map(tourMapper::toSummaryDto);
        } else if (hasLevel) {
            return tourRepository.findByActivityLevel(
                    ActivityLevel.valueOf(filter.activityLevel().toUpperCase()), pageable)
                    .map(tourMapper::toSummaryDto);
        } else {
            return tourRepository.findAll(pageable).map(tourMapper::toSummaryDto);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TourSummaryDto> getToursByPort(String portCode, Pageable pageable) {
        if (!portRepository.existsByPortCode(portCode)) {
            throw new ResourceNotFoundException("Port", portCode);
        }
        return tourRepository.findByPortCode(portCode, pageable).map(tourMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public TourResponseDto updateTour(UUID tourId, TourRequestDto dto, String currentUser) {
        Tour tour = tourRepository.findByTourIdWithImages(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
        if (!portRepository.existsByPortCode(dto.portCode())) {
            throw new BusinessRuleViolationException("Port '" + dto.portCode() + "' does not exist");
        }
        Object oldValue = tourMapper.toResponseDto(tour);
        tourMapper.updateEntityFromDto(dto, tour);
        Tour saved = tourRepository.save(tour);
        log.info("Tour updated: tourId={} by userId-ref={}", tourId, currentUser);
        auditService.log("TOUR", tourId.toString(), AuditAction.UPDATE, currentUser, oldValue, dto);
        return buildResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTour(UUID tourId, String currentUser) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
        Object oldValue = tourMapper.toResponseDto(tour);
        tourRepository.delete(tour);
        log.info("Tour deleted: tourId={} by userId-ref={}", tourId, currentUser);
        auditService.log("TOUR", tourId.toString(), AuditAction.DELETE, currentUser, oldValue, null);
    }

    private TourResponseDto buildResponse(Tour tour) {
        TourResponseDto base = tourMapper.toResponseDto(tour);
        String portName = portRepository.findById(tour.getPortCode())
                .map(p -> p.getName()).orElse(null);
        return new TourResponseDto(
                base.tourId(), base.portCode(), portName, base.tourName(),
                base.durationMinutes(), base.category(), base.description(),
                base.highlights(), base.bookingConditions(), base.mealIncluded(),
                base.activityLevel(), base.accessibilityInfo(), base.images(),
                base.createdAt(), base.updatedAt(), base.createdBy(), base.updatedBy(),
                base.version()
        );
    }
}
