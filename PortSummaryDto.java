package com.shoreexcursions.app.port.dto;

/**
 * Summary DTO for port list views.
 *
 * @see US-009, US-011, API-004
 */
public record PortSummaryDto(
        String portCode,
        String name,
        String region,
        long tourCount
) {}
