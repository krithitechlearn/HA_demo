package com.shoreexcursions.app.auth.service.impl;

import com.shoreexcursions.app.audit.AuditAction;
import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.auth.dto.PasswordResetDto;
import com.shoreexcursions.app.auth.service.AuthService;
import com.shoreexcursions.app.exception.UnauthorizedException;
import com.shoreexcursions.app.security.JwtTokenProvider;
import com.shoreexcursions.app.security.SecurityConstants;
import com.shoreexcursions.app.user.domain.AppUser;
import com.shoreexcursions.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for authentication operations.
 * Handles login with lockout, JWT generation, and password reset stub.
 *
 * @see US-037, US-046, LLD Section 6.3
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        AppUser user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> {
                    log.debug("Login failed: user not found for username");
                    return new UnauthorizedException("Invalid credentials");
                });

        // Check account lock
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            auditService.log("APP_USER", user.getUserId().toString(),
                    AuditAction.LOGIN_FAILED, user.getUsername(), null, "Account locked");
            throw new UnauthorizedException("Account is locked. Please try again later.", "ACCOUNT_LOCKED");
        }

        // Check account enabled
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled", "ACCOUNT_LOCKED");
        }

        // Validate password
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= SecurityConstants.MAX_FAILED_LOGIN_ATTEMPTS) {
                user.setLockedUntil(OffsetDateTime.now()
                        .plusMinutes(SecurityConstants.ACCOUNT_LOCK_DURATION_MINUTES));
                log.info("Account locked after {} failed attempts for userId={}",
                        attempts, user.getUserId());
            }
            userRepository.save(user);
            auditService.log("APP_USER", user.getUserId().toString(),
                    AuditAction.LOGIN_FAILED, user.getUsername(), null, "Invalid password");
            throw new UnauthorizedException("Invalid credentials");
        }

        // Reset failed attempts on success
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        List<String> roles = new ArrayList<>(user.getRoles());
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);
        auditService.log("APP_USER", user.getUserId().toString(),
                AuditAction.LOGIN, user.getUsername(), null, null);

        return new LoginResponseDto(
                token,
                "Bearer",
                jwtTokenProvider.getExpirationMs(),
                user.getUsername(),
                roles
        );
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // Always return 202 regardless of email existence (security - A-08)
        // Stub: email service deferred per A-08
        userRepository.findByEmail(email).ifPresent(user ->
                log.info("Password reset requested for userId={}", user.getUserId()));
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDto dto) {
        // Stub: password reset token management deferred per A-08
        // Token validation would be implemented when email service is added
        throw new UnauthorizedException("Password reset via token is not yet implemented", "TOKEN_EXPIRED");
    }
}
