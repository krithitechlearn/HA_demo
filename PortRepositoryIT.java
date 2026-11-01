package com.shoreexcursions.app.port.repository;

import com.shoreexcursions.app.port.domain.Port;
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

import static org.assertj.core.api.Assertions.*;

/**
 * Repository integration tests for PortRepository using Testcontainers.
 *
 * @see US-001, US-002, US-009, US-024
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PortRepositoryIT {

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
    private PortRepository portRepository;

    @Test
    @DisplayName("US-001: save_validPort_persistsSuccessfully")
    void save_validPort_persistsSuccessfully() {
        Port port = buildPort("MIAMI", "Miami", "Caribbean");

        Port saved = portRepository.save(port);

        assertThat(saved.getPortCode()).isEqualTo("MIAMI");
        assertThat(portRepository.existsByPortCode("MIAMI")).isTrue();
    }

    @Test
    @DisplayName("US-024: save_duplicatePortCode_throwsException")
    void save_duplicatePortCode_throwsException() {
        portRepository.save(buildPort("MIAMI", "Miami", "Caribbean"));

        Port duplicate = buildPort("MIAMI", "Miami Duplicate", "Caribbean");
        assertThatThrownBy(() -> portRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("US-009: findByRegionIgnoreCase_returnsMatchingPorts")
    void findByRegionIgnoreCase_returnsMatchingPorts() {
        portRepository.save(buildPort("MIAMI", "Miami", "Caribbean"));
        portRepository.save(buildPort("NASSAU", "Nassau", "Caribbean"));
        portRepository.save(buildPort("OSLO", "Oslo", "Nordic"));

        Page<Port> result = portRepository.findByRegionIgnoreCase("caribbean", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("US-002: findByPortCodeWithImages_returnsPortWithImages")
    void findByPortCodeWithImages_returnsPortWithImages() {
        portRepository.save(buildPort("MIAMI", "Miami", "Caribbean"));

        Optional<Port> result = portRepository.findByPortCodeWithImages("MIAMI");

        assertThat(result).isPresent();
        assertThat(result.get().getImages()).isNotNull();
    }

    private Port buildPort(String code, String name, String region) {
        Port port = new Port();
        port.setPortCode(code);
        port.setName(name);
        port.setRegion(region);
        return port;
    }
}
