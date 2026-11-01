package com.shoreexcursions.app.exception;

/**
 * Thrown when a create operation would violate a uniqueness constraint.
 * Maps to HTTP 409 CONFLICT.
 *
 * @see LLD Section 6.6
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
