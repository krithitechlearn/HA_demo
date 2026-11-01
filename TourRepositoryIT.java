package com.shoreexcursions.app.tour.repository;

import com.shoreexcursions.app.port.domain.Port;
import com.shoreexcursions.app.port.repository.PortRepository;
import com.shoreexcursions.app.tour.domain.ActivityLevel;
import com.shoreexcursions.app.tour.domain.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository integration tests for TourRepository using Testcontainers.
 *
 * @see US-005, US-006, US-010, US-025, US-026
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TourRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("shoreexcursions_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private PortRepository portRepository;

    @BeforeEach
    void setUp() {
        Port port = new Port();
        port.setPortCode("MIAMI");
        port.setName("Miami");
        port.setRegion("Caribbean");
        portRepository.save(port);
    }

    @Test
    @DisplayName("US-005: save_validTour_persistsSuccessfully")
    void save_validTour_persistsSuccessfully() {
        Tour tour = buildTour("MIAMI", "City Tour", "Culture", ActivityLevel.LOW);

        Tour saved = tourRepository.save(tour);

        assertThat(saved.getTourId()).isNotNull();
        assertThat(saved.getTourName()).isEqualTo("City Tour");
    }

    @Test
    @DisplayName("US-026: save_tourWithInvalidPortCode_throwsException")
    void save_tourWithInvalidPortCode_throwsException() {
        Tour tour = buildTour("INVALID", "Tour", "Culture", ActivityLevel.LOW);

        assertThatThrownBy(() -> tourRepository.saveAndFlush(tour))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("US-010: findByPortCode_returnsToursForPort")
    void findByPortCode_returnsToursForPort() {
        tourRepository.save(buildTour("MIAMI", "Tour 1", "Culture", ActivityLevel.LOW));
        tourRepository.save(buildTour("MIAMI", "Tour 2", "Adventure", ActivityLevel.HIGH));

        Page<Tour> result = tourRepository.findByPortCode("MIAMI", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("US-006: findByTourIdWithImages_returnsTourWithImages")
    void findByTourIdWithImages_returnsTourWithImages() {
        Tour saved = tourRepository.save(buildTour("MIAMI", "City Tour", "Culture", ActivityLevel.LOW));

        Optional<Tour> result = tourRepository.findByTourIdWithImages(saved.getTourId());

        assertThat(result).isPresent();
        assertThat(result.get().getImages()).isNotNull();
    }

    private Tour buildTour(String portCode, String name, String category, ActivityLevel level) {
        Tour tour = new Tour();
        tour.setPortCode(portCode);
        tour.setTourName(name);
        tour.setCategory(category);
        tour.setActivityLevel(level);
        tour.setMealIncluded(false);
        return tour;
    }
}
