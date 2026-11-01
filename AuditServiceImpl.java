package com.shoreexcursions.app.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Service implementation for persisting audit log entries.
 * Uses REQUIRES_NEW propagation to ensure audit log is written
 * even if the parent transaction rolls back.
 *
 * @see US-045, LLD Section 6.3
 */
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String entityType, String entityId, AuditAction action,
                    String performedBy, Object oldValue, Object newValue) {
        AuditLogEntity entry = new AuditLogEntity();
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setAction(action);
        entry.setPerformedBy(performedBy);
        entry.setPerformedAt(OffsetDateTime.now());
        entry.setOldValue(toJson(oldValue));
        entry.setNewValue(toJson(newValue));
        auditLogRepository.save(entry);
        log.info("Audit: action={} entityType={} entityId={} performedBy={}",
                action, entityType, entityId, performedBy);
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit value: {}", e.getMessage());
            return value.toString();
        }
    }
}
