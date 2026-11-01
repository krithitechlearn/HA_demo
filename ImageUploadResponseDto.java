package com.shoreexcursions.app.image.dto;

import java.util.UUID;

/**
 * Response DTO returned after a successful image upload.
 *
 * @see US-031, API-015, API-016, API-017, API-018
 */
public record ImageUploadResponseDto(
        UUID imageId,
        String imageUrl,
        int displayOrder
) {}
