package com.shoreexcursions.app.tour.controller;

import com.shoreexcursions.app.common.PagedResponse;
import com.shoreexcursions.app.tour.dto.TourFilterDto;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import com.shoreexcursions.app.tour.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for tour management.
 *
 * @see US-005, US-006, US-007, US-008, US-010, US-015, US-016, US-017, US-018, US-019
 * @see API-009, API-010, API-011, API-012, API-013, API-014
 */
@RestController
@Tag(name = "Tours", description = "Shore excursion tour management")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    /**
     * Lists all tours with optional filters and pagination.
     * API-009
     */
    @GetMapping("/api/v1/tours")
    @Operation(summary = "List all tours", description = "API-009: List all tours with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tours retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PagedResponse<TourSummaryDto>> getAllTours(
            @RequestParam(required = false) String portCode,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String activityLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tourName,asc") String sort) {
        size = Math.min(size, 100);
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        TourFilterDto filter = new TourFilterDto(portCode, category, activityLevel);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "max-age=60, must-revalidate");
        return ResponseEntity.ok().headers(headers)
                .body(PagedResponse.from(tourService.getAllTours(filter, pageable)));
    }

    /**
     * Gets a tour by its ID.
     * API-010
     */
    @GetMapping("/api/v1/tours/{tourId}")
    @Operation(summary = "Get tour by ID", description = "API-010: Get tour details by tour ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<TourResponseDto> getTour(@PathVariable UUID tourId) {
        return ResponseEntity.ok(tourService.getTour(tourId));
    }

    /**
     * Creates a new tour.
     * API-011
     */
    @PostMapping("/api/v1/tours")
    @PreAuthorize("hasAnyRole('EDITOR', 'DB_ADMIN')")
    @Operation(summary = "Create tour", description = "API-011: Create a new tour record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tour created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Port not found")
    })
    public ResponseEntity<TourResponseDto> createTour(
            @Valid @RequestBody TourRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        TourResponseDto created = tourService.createTour(dto, userDetails.getUsername());
        URI location = ServletUriComponentsBuilder
                .fromUriString("/api/v1/tours/{tourId}")
                .buildAndExpand(created.tourId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing tour.
     * API-012
     */
    @PutMapping("/api/v1/tours/{tourId}")
    @PreAuthorize("hasAnyRole('EDITOR', 'DB_ADMIN')")
    @Operation(summary = "Update tour", description = "API-012: Update an existing tour record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Tour not found"),
            @ApiResponse(responseCode = "409", description = "Optimistic lock conflict")
    })
    public ResponseEntity<TourResponseDto> updateTour(
            @PathVariable UUID tourId,
            @Valid @RequestBody TourRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tourService.updateTour(tourId, dto, userDetails.getUsername()));
    }

    /**
     * Deletes a tour.
     * API-013
     */
    @DeleteMapping("/api/v1/tours/{tourId}")
    @PreAuthorize("hasRole('DB_ADMIN')")
    @Operation(summary = "Delete tour", description = "API-013: Delete a tour record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tour deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<Void> deleteTour(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal UserDetails userDetails) {
        tourService.deleteTour(tourId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets tours by port code.
     * API-014
     */
    @GetMapping("/api/v1/ports/{portCode}/tours")
    @Operation(summary = "Get tours by port", description = "API-014: Get all tours for a specific port")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tours retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Port not found")
    })
    public ResponseEntity<PagedResponse<TourSummaryDto>> getToursByPort(
            @PathVariable String portCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tourName,asc") String sort) {
        size = Math.min(size, 100);
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        return ResponseEntity.ok(PagedResponse.from(tourService.getToursByPort(portCode, pageable)));
    }
}
