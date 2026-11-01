package com.shoreexcursions.app.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.auth.service.AuthService;
import com.shoreexcursions.app.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc controller tests for AuthController.
 *
 * @see US-037, US-046, API-001, API-002, API-003
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("API-001: login_validCredentials_returns200WithToken")
    void login_validCredentials_returns200WithToken() throws Exception {
        LoginRequestDto request = new LoginRequestDto("admin", "password");
        LoginResponseDto response = new LoginResponseDto("jwt-token", "Bearer",
                28800000L, "admin", List.of("ROLE_DB_ADMIN"));
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("API-001: login_invalidCredentials_returns401")
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequestDto request = new LoginRequestDto("admin", "wrong");
        when(authService.login(any())).thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("API-001: login_missingUsername_returns400")
    void login_missingUsername_returns400() throws Exception {
        LoginRequestDto request = new LoginRequestDto("", "password");

        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("API-002: requestPasswordReset_validEmail_returns202")
    void requestPasswordReset_validEmail_returns202() throws Exception {
        doNothing().when(authService).requestPasswordReset(any());

        mockMvc.perform(post("/api/v1/auth/password-reset/request").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\"}"))
                .andExpect(status().isAccepted());
    }
}
