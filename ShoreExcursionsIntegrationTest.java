package com.shoreexcursions.app;

import com.shoreexcursions.app.auth.dto.LoginRequestDto;
import com.shoreexcursions.app.auth.dto.LoginResponseDto;
import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test for the main flow: Login -> Create Port -> Get Port.
 * Uses Testcontainers for a real PostgreSQL instance.
 *
 * @see US-037, US-001, US-002
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ShoreExcursionsIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("shoreexcursions_it")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("app.jwt.secret", () -> "integration-test-secret-key-minimum-256-bits-required");
        registry.add("app.jwt.expiration-ms", () -> "28800000");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("US-037 + US-001 + US-002: login_createPort_getPort_mainFlow")
    void login_createPort_getPort_mainFlow() {
        // Step 1: Login
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "ChangeMe@12345");
        ResponseEntity<LoginResponseDto> loginResponse = restTemplate.postForEntity(
                "/api/v1/auth/login", loginRequest, LoginResponseDto.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody().accessToken();
        assertThat(token).isNotBlank();

        // Step 2: Create Port
        PortRequestDto portRequest = new PortRequestDto("MIAMI", "Miami", "Caribbean",
                "Sunny port", null, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PortRequestDto> entity = new HttpEntity<>(portRequest, headers);
        ResponseEntity<PortResponseDto> createResponse = restTemplate.postForEntity(
                "/api/v1/ports", entity, PortResponseDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().portCode()).isEqualTo("MIAMI");

        // Step 3: Get Port
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<PortResponseDto> getResponse = restTemplate.exchange(
                "/api/v1/ports/MIAMI", HttpMethod.GET, getEntity, PortResponseDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name()).isEqualTo("Miami");
    }
}
