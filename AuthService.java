package com.shoreexcursions.app.auth.service;

import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.auth.dto.PasswordResetDto;

/**
 * Service interface for authentication operations.
 *
 * @see US-037, US-046, LLD Section 6.3
 */
public interface AuthService {

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param dto login credentials
     * @return LoginResponseDto with JWT token and user info
     * @see US-037, API-001
     */
    LoginResponseDto login(LoginRequestDto dto);

    /**
     * Initiates a password reset flow by generating a time-limited token.
     * Always returns successfully regardless of email existence (security).
     *
     * @param email the user's email address
     * @see US-046, API-002
     */
    void requestPasswordReset(String email);

    /**
     * Confirms a password reset using the provided token and new password.
     *
     * @param dto token and new password
     * @see US-046, API-003
     */
    void resetPassword(PasswordResetDto dto);
}
