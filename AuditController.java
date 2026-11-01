package com.shoreexcursions.app.audit.controller;

import com.shoreexcursions.app.audit.AuditLogEntity;
import com.shoreexcursions.app.audit.AuditLogRepository;
import com.shoreexcursions.app.audit.dto.AuditLogDto;
import com.shoreexcursions.app.audit.mapper.AuditLogMapper;
import com.shoreexcursions.app.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for querying audit logs (DB_ADMIN only).
 *
 * @see US-045, API-025
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Audit log query - DB_ADMIN only")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DB_ADMIN')")
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    /**
     * Queries audit logs with optional filters.
     * API-025
     */
    @GetMapping
    @Operation(summary = "Query audit logs", description = "API-025: Query audit logs (DB_ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PagedResponse<AuditLogDto>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String performedBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "performedAt"));
        Page<AuditLogEntity> result;
        if (entityType != null && performedBy != null) {
            result = auditLogRepository.findByEntityType(entityType, pageable);
        } else if (entityType != null) {
            result = auditLogRepository.findByEntityType(entityType, pageable);
        } else if (performedBy != null) {
            result = auditLogRepository.findByPerformedBy(performedBy, pageable);
        } else {
            result = auditLogRepository.findAll(pageable);
        }
        return ResponseEntity.ok(PagedResponse.from(result.map(auditLogMapper::toDto)));
    }
}
