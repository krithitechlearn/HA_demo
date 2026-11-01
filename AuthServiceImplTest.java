package com.shoreexcursions.app.auth.service;

import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.auth.service.impl.AuthServiceImpl;
import com.shoreexcursions.app.exception.UnauthorizedException;
import com.shoreexcursions.app.security.JwtTokenProvider;
import com.shoreexcursions.app.user.domain.AppUser;
import com.shoreexcursions.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl.
 *
 * @see US-037, US-046
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setUserId(UUID.randomUUID());
        user.setUsername("admin");
        user.setPasswordHash("$2a$12$hashed");
        user.setEnabled(true);
        user.setFailedLoginAttempts(0);
        user.setRoles(Set.of("ROLE_DB_ADMIN"));
    }

    @Test
    @DisplayName("US-037: login_validCredentials_returnsToken")
    void login_validCredentials_returnsToken() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "$2a$12$hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(28800000L);
        when(userRepository.save(any())).thenReturn(user);

        LoginResponseDto result = authService.login(new LoginRequestDto("admin", "password"));

        assertThat(result.accessToken()).isEqualTo("jwt-token");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.username()).isEqualTo("admin");
    }

    @Test
    @DisplayName("US-037: login_userNotFound_throwsUnauthorizedException")
    void login_userNotFound_throwsUnauthorizedException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("unknown", "password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("US-037: login_wrongPassword_incrementsFailedAttempts")
    void login_wrongPassword_incrementsFailedAttempts() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$12$hashed")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("admin", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("US-037: login_accountLocked_throwsUnauthorizedException")
    void login_accountLocked_throwsUnauthorizedException() {
        user.setLockedUntil(OffsetDateTime.now().plusMinutes(20));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("admin", "password")))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(e -> assertThat(((UnauthorizedException) e).getErrorCode()).isEqualTo("ACCOUNT_LOCKED"));
    }

    @Test
    @DisplayName("US-037: login_fiveFailedAttempts_locksAccount")
    void login_fiveFailedAttempts_locksAccount() {
        user.setFailedLoginAttempts(4);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$12$hashed")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("admin", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
        assertThat(user.getLockedUntil()).isNotNull();
    }
}
