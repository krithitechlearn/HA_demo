package com.shoreexcursions.app.audit;

/**
 * Enum representing the type of action recorded in the audit log.
 *
 * @see LLD Section 5.3
 */
public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE,
    LOGIN,
    LOGIN_FAILED,
    LOGOUT
}
