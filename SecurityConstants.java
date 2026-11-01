package com.shoreexcursions.app.security;

/**
 * Security-related constants for JWT and role names.
 *
 * @see LLD Section 9
 */
public final class SecurityConstants {

    private SecurityConstants() {}

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String ROLE_DB_ADMIN = "ROLE_DB_ADMIN";
    public static final String ROLE_EDITOR = "ROLE_EDITOR";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_VIEWER = "ROLE_VIEWER";
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
}
