package com.shoreexcursions.app.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response schema returned by the GlobalExceptionHandler.
 * Matches the error contract defined in LLD Section 6.6.
 *
 * @see US-020
 */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        String traceId,
        List<FieldError> fieldErrors
) {
    /**
     * Represents a single field-level validation error.
     */
    public record FieldError(
            String field,
            String message
    ) {}
}
