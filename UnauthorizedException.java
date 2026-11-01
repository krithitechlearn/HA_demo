package com.shoreexcursions.app.exception;

/**
 * Thrown when authentication fails or a JWT token is invalid/missing.
 * Maps to HTTP 401 UNAUTHORIZED.
 *
 * @see LLD Section 6.6
 */
public class UnauthorizedException extends RuntimeException {

    private final String errorCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
