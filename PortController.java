package com.shoreexcursions.app.port.controller;

import com.shoreexcursions.app.common.PagedResponse;
import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import com.shoreexcursions.app.port.dto.PortSummaryDto;
import com.shoreexcursions.app.port.service.PortService;
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

/**
 * REST controller for port management.
 *
 * @see US-001, US-002, US-003, US-004, US-009, US-011, US-012, US-013, US-014
 * @see API-004, API-005, API-006, API-007, API-008
 */
@RestController
@RequestMapping("/api/v1/ports")
@Tag(name = "Ports", description = "Shore excursion port management")
@RequiredArgsConstructor
public class PortController {

    private final PortService portService;

    /**
     * Lists all ports with optional region filter and pagination.
     * API-004
     */
    @GetMapping
    @Operation(summary = "List all ports", description = "API-004: List all ports with optional region filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ports retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PagedResponse<PortSummaryDto>> getAllPorts(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        size = Math.min(size, 100);
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "max-age=60, must-revalidate");
        return ResponseEntity.ok().headers(headers)
                .body(PagedResponse.from(portService.getAllPorts(region, pageable)));
    }

    /**
     * Gets a port by its code.
     * API-005
     */
    @GetMapping("/{portCode}")
    @Operation(summary = "Get port by code", description = "API-005: Get port details by port code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Port found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Port not found")
    })
    public ResponseEntity<PortResponseDto> getPort(@PathVariable String portCode) {
        return ResponseEntity.ok(portService.getPort(portCode));
    }

    /**
     * Creates a new port.
     * API-006
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'DB_ADMIN')")
    @Operation(summary = "Create port", description = "API-006: Create a new port record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Port created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Port code already exists")
    })
    public ResponseEntity<PortResponseDto> createPort(
            @Valid @RequestBody PortRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PortResponseDto created = portService.createPort(dto, userDetails.getUsername());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{portCode}").buildAndExpand(created.portCode()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing port.
     * API-007
     */
    @PutMapping("/{portCode}")
    @PreAuthorize("hasAnyRole('EDITOR', 'DB_ADMIN')")
    @Operation(summary = "Update port", description = "API-007: Update an existing port record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Port updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Port not found")
    })
    public ResponseEntity<PortResponseDto> updatePort(
            @PathVariable String portCode,
            @Valid @RequestBody PortRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(portService.updatePort(portCode, dto, userDetails.getUsername()));
    }

    /**
     * Deletes a port.
     * API-008
     */
    @DeleteMapping("/{portCode}")
    @PreAuthorize("hasRole('DB_ADMIN')")
    @Operation(summary = "Delete port", description = "API-008: Delete a port record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Port deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Port not found"),
            @ApiResponse(responseCode = "409", description = "Port has associated tours")
    })
    public ResponseEntity<Void> deletePort(
            @PathVariable String portCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        portService.deletePort(portCode, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
