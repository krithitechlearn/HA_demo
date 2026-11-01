package com.shoreexcursions.app.port.mapper;

import com.shoreexcursions.app.port.domain.Port;
import com.shoreexcursions.app.port.domain.PortImage;
import com.shoreexcursions.app.port.dto.PortImageDto;
import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import com.shoreexcursions.app.port.dto.PortSummaryDto;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Port entity and DTOs.
 * Uses Spring component model for dependency injection.
 *
 * @see LLD Section 6.1, 6.2
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PortMapper {

    /**
     * Maps a PortRequestDto to a Port entity.
     * Ignores audit fields (set by JPA auditing).
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "tours", ignore = true)
    Port toEntity(PortRequestDto dto);

    /**
     * Maps a Port entity to a PortResponseDto.
     * tourCount is set by the service layer.
     */
    @Mapping(target = "tourCount", ignore = true)
    PortResponseDto toResponseDto(Port port);

    /**
     * Maps a Port entity to a PortSummaryDto.
     * tourCount is set by the service layer.
     */
    @Mapping(target = "tourCount", ignore = true)
    PortSummaryDto toSummaryDto(Port port);

    PortImageDto toImageDto(PortImage image);

    List<PortImageDto> toImageDtoList(List<PortImage> images);

    /**
     * Updates an existing Port entity from a PortRequestDto.
     * portCode is not updated (business rule).
     */
    @Mapping(target = "portCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "tours", ignore = true)
    void updateEntityFromDto(PortRequestDto dto, @MappingTarget Port port);
}
