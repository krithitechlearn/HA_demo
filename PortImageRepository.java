package com.shoreexcursions.app.port.repository;

import com.shoreexcursions.app.port.domain.PortImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for PortImage entities.
 *
 * @see US-031
 */
@Repository
public interface PortImageRepository extends JpaRepository<PortImage, UUID> {

    List<PortImage> findByPortCodeOrderByDisplayOrderAsc(String portCode);

    long countByPortCode(String portCode);

    void deleteByPortCode(String portCode);
}
