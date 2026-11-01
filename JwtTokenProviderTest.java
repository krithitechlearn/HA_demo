package com.shoreexcursions.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 *
 * @see US-037, US-043
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "test-secret-key-for-unit-testing-minimum-256-bits-required-here");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 28800000L);
    }

    @Test
    @DisplayName("US-037: generateToken_validUser_returnsNonEmptyToken")
    void generateToken_validUser_returnsNonEmptyToken() {
        UserDetails userDetails = buildUser("testuser");

        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("US-043: validateToken_validToken_returnsTrue")
    void validateToken_validToken_returnsTrue() {
        UserDetails userDetails = buildUser("testuser");
        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("US-043: validateToken_invalidToken_returnsFalse")
    void validateToken_invalidToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("US-037: getUsernameFromToken_validToken_returnsUsername")
    void getUsernameFromToken_validToken_returnsUsername() {
        UserDetails userDetails = buildUser("testuser");
        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("testuser");
    }

    private UserDetails buildUser(String username) {
        return User.builder()
                .username(username)
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
