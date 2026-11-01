package com.shoreexcursions.app.exception;

/**
 * Thrown when a requested resource cannot be found.
 * Maps to HTTP 404 NOT FOUND.
 *
 * @see LLD Section 6.6
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(resourceType + " not found with identifier: " + identifier);
    }
}
