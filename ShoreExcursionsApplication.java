package com.shoreexcursions.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Shore Excursions Application.
 * Modernizes the legacy IBM Domino application with a Spring Boot 3.x backend.
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class ShoreExcursionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoreExcursionsApplication.class, args);
    }
}
