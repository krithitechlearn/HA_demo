package com.shoreexcursions.app.audit;

/**
 * Service interface for writing audit log entries.
 *
 * @see US-045, LLD Section 6.3
 */
public interface AuditService {

    /**
     * Logs an audit event for a mutable operation.
     *
     * @param entityType  the type of entity (e.g., "PORT", "TOUR")
     * @param entityId    the identifier of the entity
     * @param action      the action performed
     * @param performedBy the username of the actor
     * @param oldValue    JSON snapshot of the old state (nullable)
     * @param newValue    JSON snapshot of the new state (nullable)
     */
    void log(String entityType, String entityId, AuditAction action,
             String performedBy, Object oldValue, Object newValue);
}
