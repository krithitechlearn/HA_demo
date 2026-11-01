package com.shoreexcursions.app.image.service.impl;

import com.shoreexcursions.app.exception.BusinessRuleViolationException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.image.dto.ImageUploadResponseDto;
import com.shoreexcursions.app.image.service.ImageStorageService;
import com.shoreexcursions.app.port.domain.PortImage;
import com.shoreexcursions.app.port.repository.PortImageRepository;
import com.shoreexcursions.app.port.repository.PortRepository;
import com.shoreexcursions.app.tour.domain.TourImage;
import com.shoreexcursions.app.tour.repository.TourImageRepository;
import com.shoreexcursions.app.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Local/stub implementation of ImageStorageService.
 * Stores image URL references in the database.
 * Actual file storage is provider-agnostic (configurable via IMAGE_STORAGE_BASE_URL).
 *
 * @see US-031, A-04, Q-06
 */
@Service
@RequiredArgsConstructor
public class LocalImageStorageServiceImpl implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalImageStorageServiceImpl.class);
    private static final int MAX_IMAGES = 10;
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif"};

    private final PortRepository portRepository;
    private final PortImageRepository portImageRepository;
    private final TourRepository tourRepository;
    private final TourImageRepository tourImageRepository;

    @Value("${app.image.storage.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public ImageUploadResponseDto storePortImage(MultipartFile file, String portCode) {
        portRepository.findById(portCode)
                .orElseThrow(() -> new ResourceNotFoundException("Port", portCode));
        validateFile(file);
        long count = portImageRepository.countByPortCode(portCode);
        if (count >= MAX_IMAGES) {
            throw new BusinessRuleViolationException(
                    "Maximum " + MAX_IMAGES + " images allowed per port");
        }
        String imageUrl = buildImageUrl(file.getOriginalFilename());
        PortImage image = new PortImage();
        image.setPortCode(portCode);
        image.setImageUrl(imageUrl);
        image.setDisplayOrder((int) count);
        PortImage saved = portImageRepository.save(image);
        log.info("Port image stored: portCode={} imageId={}", portCode, saved.getId());
        return new ImageUploadResponseDto(saved.getId(), saved.getImageUrl(), saved.getDisplayOrder());
    }

    @Override
    @Transactional
    public void deletePortImage(String portCode, UUID imageId) {
        PortImage image = portImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("PortImage", imageId));
        if (!image.getPortCode().equals(portCode)) {
            throw new ResourceNotFoundException("PortImage", imageId);
        }
        portImageRepository.delete(image);
        log.info("Port image deleted: portCode={} imageId={}", portCode, imageId);
    }

    @Override
    @Transactional
    public ImageUploadResponseDto storeTourImage(MultipartFile file, UUID tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
        validateFile(file);
        long count = tourImageRepository.countByTourId(tourId);
        if (count >= MAX_IMAGES) {
            throw new BusinessRuleViolationException(
                    "Maximum " + MAX_IMAGES + " images allowed per tour");
        }
        String imageUrl = buildImageUrl(file.getOriginalFilename());
        TourImage image = new TourImage();
        image.setTourId(tourId);
        image.setImageUrl(imageUrl);
        image.setDisplayOrder((int) count);
        TourImage saved = tourImageRepository.save(image);
        log.info("Tour image stored: tourId={} imageId={}", tourId, saved.getId());
        return new ImageUploadResponseDto(saved.getId(), saved.getImageUrl(), saved.getDisplayOrder());
    }

    @Override
    @Transactional
    public void deleteTourImage(UUID tourId, UUID imageId) {
        TourImage image = tourImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("TourImage", imageId));
        if (!image.getTourId().equals(tourId)) {
            throw new ResourceNotFoundException("TourImage", imageId);
        }
        tourImageRepository.delete(image);
        log.info("Tour image deleted: tourId={} imageId={}", tourId, imageId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleViolationException("File must not be empty");
        }
        String contentType = file.getContentType();
        boolean valid = false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new BusinessRuleViolationException(
                    "Invalid file type. Allowed types: JPEG, PNG, GIF");
        }
    }

    private String buildImageUrl(String originalFilename) {
        String uniqueName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "image");
        return baseUrl + "/" + uniqueName;
    }
}
