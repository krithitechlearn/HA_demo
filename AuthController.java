package com.shoreexcursions.app.auth.controller;

import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.auth.dto.PasswordResetDto;
import com.shoreexcursions.app.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 *
 * @see US-037, US-046, API-001, API-002, API-003
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and password management endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token.
     * API-001
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "API-001: Authenticate user and return JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or account locked")
    })
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Requests a password reset link.
     * API-002
     */
    @PostMapping("/password-reset/request")
    @Operation(summary = "Request password reset", description = "API-002: Request password reset link")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Request accepted"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<Void> requestPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.requestPasswordReset(email);
        return ResponseEntity.accepted().build();
    }

    /**
     * Confirms a password reset with a token.
     * API-003
     */
    @PostMapping("/password-reset/confirm")
    @Operation(summary = "Confirm password reset", description = "API-003: Confirm password reset with token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "410", description = "Token expired or already used")
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }
}
