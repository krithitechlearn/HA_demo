package com.shoreexcursions.app.user.service.impl;

import com.shoreexcursions.app.audit.AuditAction;
import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.exception.DuplicateResourceException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.user.domain.AppUser;
import com.shoreexcursions.app.user.dto.RoleAssignmentDto;
import com.shoreexcursions.app.user.dto.UserRequestDto;
import com.shoreexcursions.app.user.dto.UserResponseDto;
import com.shoreexcursions.app.user.mapper.UserMapper;
import com.shoreexcursions.app.user.repository.UserRepository;
import com.shoreexcursions.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service implementation for user management.
 *
 * @see US-038, US-039, US-048, LLD Section 6.3
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto dto, String currentUser) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("User with email already exists");
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new DuplicateResourceException("User with username '" + dto.username() + "' already exists");
        }
        AppUser user = new AppUser();
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setEnabled(dto.enabled() != null ? dto.enabled() : true);
        user.setRoles(new HashSet<>(Set.of("ROLE_USER")));
        AppUser saved = userRepository.save(user);
        log.info("User created: userId={} by userId-ref={}", saved.getUserId(), currentUser);
        auditService.log("APP_USER", saved.getUserId().toString(), AuditAction.CREATE, currentUser, null,
                new UserResponseDto(saved.getUserId(), saved.getEmail(), saved.getUsername(),
                        saved.isEnabled(), saved.getRoles(), null, null));
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID userId, UserRequestDto dto, String currentUser) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        if (StringUtils.hasText(dto.password())) {
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }
        AppUser saved = userRepository.save(user);
        log.info("User updated: userId={} by userId-ref={}", userId, currentUser);
        auditService.log("APP_USER", userId.toString(), AuditAction.UPDATE, currentUser, null, dto);
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto assignRoles(UUID userId, RoleAssignmentDto dto, String currentUser) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setRoles(new HashSet<>(dto.roles()));
        AppUser saved = userRepository.save(user);
        log.info("Roles assigned: userId={} roles={} by userId-ref={}", userId, dto.roles(), currentUser);
        auditService.log("APP_USER", userId.toString(), AuditAction.UPDATE, currentUser, null, dto);
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId, String currentUser) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        userRepository.delete(user);
        log.info("User deleted: userId={} by userId-ref={}", userId, currentUser);
        auditService.log("APP_USER", userId.toString(), AuditAction.DELETE, currentUser,
                userMapper.toResponseDto(user), null);
    }
}
