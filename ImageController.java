package com.shoreexcursions.app.image.controller;

import com.shoreexcursions.app.image.dto.ImageUploadResponseDto;
import com.shoreexcursions.app.image.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for image upload and management.
 *
 * @see US-031
 * @see API-015, API-016, API-017, API-018
 */
@RestController
@Tag(name = "Images", description = "Image upload and management for ports and tours")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EDITOR', 'DB_ADMIN')")
public class ImageController {

    private final ImageStorageService imageStorageService;

    /**
     * Uploads an image for a port.
     * API-015
     */
    @PostMapping(value = "/api/v1/ports/{portCode}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload port image", description = "API-015: Upload an image for a port")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Port not found")
    })
    public ResponseEntity<ImageUploadResponseDto> uploadPortImage(
            @PathVariable String portCode,
            @RequestParam("file") MultipartFile file) {
        ImageUploadResponseDto result = imageStorageService.storePortImage(file, portCode);
        URI location = ServletUriComponentsBuilder
                .fromUriString("/api/v1/ports/{portCode}/images/{imageId}")
                .buildAndExpand(portCode, result.imageId()).toUri();
        return ResponseEntity.created(location).body(result);
    }

    /**
     * Deletes a port image.
     * API-016
     */
    @DeleteMapping("/api/v1/ports/{portCode}/images/{imageId}")
    @Operation(summary = "Delete port image", description = "API-016: Delete a port image")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<Void> deletePortImage(
            @PathVariable String portCode,
            @PathVariable UUID imageId) {
        imageStorageService.deletePortImage(portCode, imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads an image for a tour.
     * API-017
     */
    @PostMapping(value = "/api/v1/tours/{tourId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload tour image", description = "API-017: Upload an image for a tour")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<ImageUploadResponseDto> uploadTourImage(
            @PathVariable UUID tourId,
            @RequestParam("file") MultipartFile file) {
        ImageUploadResponseDto result = imageStorageService.storeTourImage(file, tourId);
        URI location = ServletUriComponentsBuilder
                .fromUriString("/api/v1/tours/{tourId}/images/{imageId}")
                .buildAndExpand(tourId, result.imageId()).toUri();
        return ResponseEntity.created(location).body(result);
    }

    /**
     * Deletes a tour image.
     * API-018
     */
    @DeleteMapping("/api/v1/tours/{tourId}/images/{imageId}")
    @Operation(summary = "Delete tour image", description = "API-018: Delete a tour image")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<Void> deleteTourImage(
            @PathVariable UUID tourId,
            @PathVariable UUID imageId) {
        imageStorageService.deleteTourImage(tourId, imageId);
        return ResponseEntity.noContent().build();
    }
}
