package com.shoreexcursions.app.audit.dto;

import com.shoreexcursions.app.audit.AuditAction;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for audit log entries.
 *
 * @see US-045, API-025
 */
public record AuditLogDto(
        UUID id,
        String entityType,
        String entityId,
        AuditAction action,
        String performedBy,
        OffsetDateTime performedAt,
        String oldValue,
        String newValue
) {}
