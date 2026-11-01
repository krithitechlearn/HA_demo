package com.shoreexcursions.app.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for AuditLogEntity.
 * Read-only in practice; writes occur only via AuditService.
 *
 * @see US-045
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    Page<AuditLogEntity> findByEntityType(String entityType, Pageable pageable);

    Page<AuditLogEntity> findByPerformedBy(String performedBy, Pageable pageable);

    Page<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, String entityId, Pageable pageable);
}
