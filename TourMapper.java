package com.shoreexcursions.app.tour.mapper;

import com.shoreexcursions.app.tour.domain.ActivityLevel;
import com.shoreexcursions.app.tour.domain.Tour;
import com.shoreexcursions.app.tour.domain.TourImage;
import com.shoreexcursions.app.tour.dto.TourImageDto;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Tour entity and DTOs.
 *
 * @see LLD Section 6.1, 6.2
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TourMapper {

    /**
     * Maps a TourRequestDto to a Tour entity.
     * activityLevel is mapped from String to enum.
     */
    @Mapping(target = "tourId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "activityLevel", source = "activityLevel", qualifiedByName = "stringToActivityLevel")
    Tour toEntity(TourRequestDto dto);

    /**
     * Maps a Tour entity to a TourResponseDto.
     * portName is set by the service layer.
     */
    @Mapping(target = "portName", ignore = true)
    @Mapping(target = "activityLevel", source = "activityLevel", qualifiedByName = "activityLevelToString")
    TourResponseDto toResponseDto(Tour tour);

    @Mapping(target = "activityLevel", source = "activityLevel", qualifiedByName = "activityLevelToString")
    TourSummaryDto toSummaryDto(Tour tour);

    TourImageDto toImageDto(TourImage image);

    List<TourImageDto> toImageDtoList(List<TourImage> images);

    /**
     * Updates an existing Tour entity from a TourRequestDto.
     */
    @Mapping(target = "tourId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "activityLevel", source = "activityLevel", qualifiedByName = "stringToActivityLevel")
    void updateEntityFromDto(TourRequestDto dto, @MappingTarget Tour tour);

    @Named("stringToActivityLevel")
    default ActivityLevel stringToActivityLevel(String value) {
        if (value == null || value.isBlank()) return null;
        return ActivityLevel.valueOf(value.toUpperCase());
    }

    @Named("activityLevelToString")
    default String activityLevelToString(ActivityLevel level) {
        return level == null ? null : level.name();
    }
}
