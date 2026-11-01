package com.shoreexcursions.app.exception;

/**
 * Thrown when a business rule prevents an operation.
 * Maps to HTTP 409 CONFLICT.
 *
 * @see LLD Section 6.6
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
