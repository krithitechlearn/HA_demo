package com.shoreexcursions.app.port.service;

import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import com.shoreexcursions.app.port.dto.PortSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for port management operations.
 *
 * @see US-001, US-002, US-003, US-004, US-009, LLD Section 6.3
 */
public interface PortService {

    /**
     * Creates a new port record.
     *
     * @param dto         port data
     * @param currentUser the authenticated username
     * @return created PortResponseDto
     * @see US-001, API-006
     */
    PortResponseDto createPort(PortRequestDto dto, String currentUser);

    /**
     * Retrieves a port by its code including images.
     *
     * @param portCode the port code
     * @return PortResponseDto
     * @see US-002, API-005
     */
    PortResponseDto getPort(String portCode);

    /**
     * Lists all ports with optional region filter and pagination.
     *
     * @param region   optional region filter
     * @param pageable pagination parameters
     * @return page of PortSummaryDto
     * @see US-009, US-011, API-004
     */
    Page<PortSummaryDto> getAllPorts(String region, Pageable pageable);

    /**
     * Updates an existing port record.
     *
     * @param portCode    the port code
     * @param dto         updated port data
     * @param currentUser the authenticated username
     * @return updated PortResponseDto
     * @see US-003, API-007
     */
    PortResponseDto updatePort(String portCode, PortRequestDto dto, String currentUser);

    /**
     * Deletes a port record. Fails if active tours exist.
     *
     * @param portCode    the port code
     * @param currentUser the authenticated username
     * @see US-004, API-008
     */
    void deletePort(String portCode, String currentUser);
}
