package com.shoreexcursions.app.image.service;

import com.shoreexcursions.app.image.dto.ImageUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface for image storage operations.
 * Stub interface to allow future implementation with cloud object storage.
 *
 * @see US-031, A-04
 */
public interface ImageStorageService {

    /**
     * Stores an image for a port and returns the upload result.
     *
     * @param file     the image file
     * @param portCode the port code
     * @return ImageUploadResponseDto with stored URL
     */
    ImageUploadResponseDto storePortImage(MultipartFile file, String portCode);

    /**
     * Deletes a port image by ID.
     *
     * @param portCode the port code
     * @param imageId  the image UUID
     */
    void deletePortImage(String portCode, UUID imageId);

    /**
     * Stores an image for a tour and returns the upload result.
     *
     * @param file   the image file
     * @param tourId the tour UUID
     * @return ImageUploadResponseDto with stored URL
     */
    ImageUploadResponseDto storeTourImage(MultipartFile file, UUID tourId);

    /**
     * Deletes a tour image by ID.
     *
     * @param tourId  the tour UUID
     * @param imageId the image UUID
     */
    void deleteTourImage(UUID tourId, UUID imageId);
}
