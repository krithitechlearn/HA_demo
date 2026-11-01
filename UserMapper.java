package com.shoreexcursions.app.user.mapper;

import com.shoreexcursions.app.user.domain.AppUser;
import com.shoreexcursions.app.user.dto.UserResponseDto;
import org.mapstruct.*;

/**
 * MapStruct mapper for AppUser entity and DTOs.
 *
 * @see LLD Section 6.1, 6.2
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    /**
     * Maps an AppUser entity to a UserResponseDto.
     * Never includes passwordHash.
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponseDto toResponseDto(AppUser user);
}
