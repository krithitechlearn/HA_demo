package com.shoreexcursions.app.user.controller;

import com.shoreexcursions.app.common.PagedResponse;
import com.shoreexcursions.app.user.dto.RoleAssignmentDto;
import com.shoreexcursions.app.user.dto.UserRequestDto;
import com.shoreexcursions.app.user.dto.UserResponseDto;
import com.shoreexcursions.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for user management (DB_ADMIN only).
 *
 * @see US-038, US-039, US-048
 * @see API-019, API-020, API-021, API-022, API-023, API-024
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management - DB_ADMIN only")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DB_ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Lists all users with pagination.
     * API-019
     */
    @GetMapping
    @Operation(summary = "List all users", description = "API-019: List all users (DB_ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<PagedResponse<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(PagedResponse.from(userService.getAllUsers(pageable)));
    }

    /**
     * Gets a user by ID.
     * API-020
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "API-020: Get user details by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * Creates a new user.
     * API-021
     */
    @PostMapping
    @Operation(summary = "Create user", description = "API-021: Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto created = userService.createUser(dto, userDetails.getUsername());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}").buildAndExpand(created.userId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing user.
     * API-022
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "API-022: Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, dto, userDetails.getUsername()));
    }

    /**
     * Assigns roles to a user.
     * API-023
     */
    @PutMapping("/{userId}/roles")
    @Operation(summary = "Assign roles", description = "API-023: Assign roles to a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles assigned"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> assignRoles(
            @PathVariable UUID userId,
            @Valid @RequestBody RoleAssignmentDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.assignRoles(userId, dto, userDetails.getUsername()));
    }

    /**
     * Deletes a user.
     * API-024
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "API-024: Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
