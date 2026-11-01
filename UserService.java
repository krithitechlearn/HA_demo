package com.shoreexcursions.app.user.service;

import com.shoreexcursions.app.user.dto.RoleAssignmentDto;
import com.shoreexcursions.app.user.dto.UserRequestDto;
import com.shoreexcursions.app.user.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for user management operations.
 *
 * @see US-038, US-039, LLD Section 6.3
 */
public interface UserService {

    UserResponseDto createUser(UserRequestDto dto, String currentUser);

    UserResponseDto getUser(UUID userId);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto updateUser(UUID userId, UserRequestDto dto, String currentUser);

    UserResponseDto assignRoles(UUID userId, RoleAssignmentDto dto, String currentUser);

    void deleteUser(UUID userId, String currentUser);
}
