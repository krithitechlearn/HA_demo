package com.shoreexcursions.app.port.repository;

import com.shoreexcursions.app.port.domain.Port;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Port entities.
 *
 * @see US-001, US-002, US-003, US-004, US-009, US-011
 */
@Repository
public interface PortRepository extends JpaRepository<Port, String> {

    boolean existsByPortCode(String portCode);

    Page<Port> findByRegionIgnoreCase(String region, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    @Query("SELECT p FROM Port p WHERE p.portCode = :portCode")
    Optional<Port> findByPortCodeWithImages(@Param("portCode") String portCode);

    @Query("SELECT COUNT(t) FROM Tour t WHERE t.portCode = :portCode")
    long countToursByPortCode(@Param("portCode") String portCode);
}
