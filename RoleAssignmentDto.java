package com.shoreexcursions.app.user.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * DTO for assigning roles to a user.
 *
 * @see US-038, US-048, API-023
 */
public record RoleAssignmentDto(
        @NotEmpty(message = "At least one role must be specified")
        Set<String> roles
) {}
