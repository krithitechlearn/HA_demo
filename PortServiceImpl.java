package com.shoreexcursions.app.port.service.impl;

import com.shoreexcursions.app.audit.AuditAction;
import com.shoreexcursions.app.audit.AuditService;
import com.shoreexcursions.app.exception.BusinessRuleViolationException;
import com.shoreexcursions.app.exception.DuplicateResourceException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.port.domain.Port;
import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import com.shoreexcursions.app.port.dto.PortSummaryDto;
import com.shoreexcursions.app.port.mapper.PortMapper;
import com.shoreexcursions.app.port.repository.PortRepository;
import com.shoreexcursions.app.port.service.PortService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service implementation for port management.
 * Enforces all port business rules per LLD Section 6.3.
 *
 * @see US-001, US-002, US-003, US-004, US-009, US-011, US-024
 */
@Service
@RequiredArgsConstructor
public class PortServiceImpl implements PortService {

    private static final Logger log = LoggerFactory.getLogger(PortServiceImpl.class);

    private final PortRepository portRepository;
    private final PortMapper portMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public PortResponseDto createPort(PortRequestDto dto, String currentUser) {
        if (portRepository.existsByPortCode(dto.portCode())) {
            throw new DuplicateResourceException(
                    "Port with code '" + dto.portCode() + "' already exists");
        }
        Port port = portMapper.toEntity(dto);
        Port saved = portRepository.save(port);
        log.info("Port created: portCode={} by userId-ref={}", saved.getPortCode(), currentUser);
        auditService.log("PORT", saved.getPortCode(), AuditAction.CREATE, currentUser, null, dto);
        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PortResponseDto getPort(String portCode) {
        Port port = portRepository.findByPortCodeWithImages(portCode)
                .orElseThrow(() -> new ResourceNotFoundException("Port", portCode));
        return buildResponse(port);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortSummaryDto> getAllPorts(String region, Pageable pageable) {
        Page<Port> ports;
        if (StringUtils.hasText(region)) {
            ports = portRepository.findByRegionIgnoreCase(region, pageable);
        } else {
            ports = portRepository.findAll(pageable);
        }
        return ports.map(port -> {
            PortSummaryDto summary = portMapper.toSummaryDto(port);
            long tourCount = portRepository.countToursByPortCode(port.getPortCode());
            return new PortSummaryDto(summary.portCode(), summary.name(), summary.region(), tourCount);
        });
    }

    @Override
    @Transactional
    public PortResponseDto updatePort(String portCode, PortRequestDto dto, String currentUser) {
        Port port = portRepository.findByPortCodeWithImages(portCode)
                .orElseThrow(() -> new ResourceNotFoundException("Port", portCode));
        Object oldValue = portMapper.toResponseDto(port);
        portMapper.updateEntityFromDto(dto, port);
        Port saved = portRepository.save(port);
        log.info("Port updated: portCode={} by userId-ref={}", portCode, currentUser);
        auditService.log("PORT", portCode, AuditAction.UPDATE, currentUser, oldValue, dto);
        return buildResponse(saved);
    }

    @Override
    @Transactional
    public void deletePort(String portCode, String currentUser) {
        Port port = portRepository.findById(portCode)
                .orElseThrow(() -> new ResourceNotFoundException("Port", portCode));
        long tourCount = portRepository.countToursByPortCode(portCode);
        if (tourCount > 0) {
            throw new BusinessRuleViolationException(
                    "Cannot delete port '" + portCode + "' because it has " + tourCount + " associated tour(s)");
        }
        Object oldValue = portMapper.toResponseDto(port);
        portRepository.delete(port);
        log.info("Port deleted: portCode={} by userId-ref={}", portCode, currentUser);
        auditService.log("PORT", portCode, AuditAction.DELETE, currentUser, oldValue, null);
    }

    private PortResponseDto buildResponse(Port port) {
        PortResponseDto base = portMapper.toResponseDto(port);
        long tourCount = portRepository.countToursByPortCode(port.getPortCode());
        return new PortResponseDto(
                base.portCode(), base.name(), base.region(), base.description(),
                base.mapLink(), base.siteHighlights(), base.funFacts(),
                base.images(), tourCount,
                base.createdAt(), base.updatedAt(), base.createdBy(), base.updatedBy()
        );
    }
}
