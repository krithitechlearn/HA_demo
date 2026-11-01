package com.shoreexcursions.app.port.service;

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
import com.shoreexcursions.app.port.service.impl.PortServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PortServiceImpl.
 *
 * @see US-001, US-002, US-003, US-004, US-009, US-024
 */
@ExtendWith(MockitoExtension.class)
class PortServiceImplTest {

    @Mock
    private PortRepository portRepository;
    @Mock
    private PortMapper portMapper;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private PortServiceImpl portService;

    private PortRequestDto requestDto;
    private Port port;
    private PortResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new PortRequestDto("MIAMI", "Miami", "Caribbean", null, null, null, null);
        port = new Port();
        port.setPortCode("MIAMI");
        port.setName("Miami");
        port.setRegion("Caribbean");
        responseDto = new PortResponseDto("MIAMI", "Miami", "Caribbean", null, null, null, null,
                Collections.emptyList(), 0L, null, null, "SYSTEM", "SYSTEM");
    }

    @Test
    @DisplayName("US-001: createPort_uniqueCode_returnsCreatedPort")
    void createPort_uniqueCode_returnsCreatedPort() {
        when(portRepository.existsByPortCode("MIAMI")).thenReturn(false);
        when(portMapper.toEntity(requestDto)).thenReturn(port);
        when(portRepository.save(port)).thenReturn(port);
        when(portMapper.toResponseDto(port)).thenReturn(responseDto);
        when(portRepository.countToursByPortCode("MIAMI")).thenReturn(0L);

        PortResponseDto result = portService.createPort(requestDto, "admin");

        assertThat(result.portCode()).isEqualTo("MIAMI");
        verify(auditService).log(eq("PORT"), eq("MIAMI"), any(), eq("admin"), isNull(), any());
    }

    @Test
    @DisplayName("US-024: createPort_duplicateCode_throwsDuplicateResourceException")
    void createPort_duplicateCode_throwsDuplicateResourceException() {
        when(portRepository.existsByPortCode("MIAMI")).thenReturn(true);

        assertThatThrownBy(() -> portService.createPort(requestDto, "admin"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("MIAMI");
    }

    @Test
    @DisplayName("US-002: getPort_existingCode_returnsPort")
    void getPort_existingCode_returnsPort() {
        when(portRepository.findByPortCodeWithImages("MIAMI")).thenReturn(Optional.of(port));
        when(portMapper.toResponseDto(port)).thenReturn(responseDto);
        when(portRepository.countToursByPortCode("MIAMI")).thenReturn(0L);

        PortResponseDto result = portService.getPort("MIAMI");

        assertThat(result.portCode()).isEqualTo("MIAMI");
    }

    @Test
    @DisplayName("US-002: getPort_nonExistingCode_throwsResourceNotFoundException")
    void getPort_nonExistingCode_throwsResourceNotFoundException() {
        when(portRepository.findByPortCodeWithImages("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portService.getPort("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("US-004: deletePort_withActiveTours_throwsBusinessRuleViolationException")
    void deletePort_withActiveTours_throwsBusinessRuleViolationException() {
        when(portRepository.findById("MIAMI")).thenReturn(Optional.of(port));
        when(portRepository.countToursByPortCode("MIAMI")).thenReturn(3L);

        assertThatThrownBy(() -> portService.deletePort("MIAMI", "admin"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("3 associated tour");
    }

    @Test
    @DisplayName("US-004: deletePort_noTours_deletesSuccessfully")
    void deletePort_noTours_deletesSuccessfully() {
        when(portRepository.findById("MIAMI")).thenReturn(Optional.of(port));
        when(portRepository.countToursByPortCode("MIAMI")).thenReturn(0L);
        when(portMapper.toResponseDto(port)).thenReturn(responseDto);

        portService.deletePort("MIAMI", "admin");

        verify(portRepository).delete(port);
        verify(auditService).log(eq("PORT"), eq("MIAMI"), any(), eq("admin"), any(), isNull());
    }

    @Test
    @DisplayName("US-009: getAllPorts_withRegionFilter_returnsFilteredPage")
    void getAllPorts_withRegionFilter_returnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Port> portPage = new PageImpl<>(List.of(port));
        PortSummaryDto summary = new PortSummaryDto("MIAMI", "Miami", "Caribbean", 0L);
        when(portRepository.findByRegionIgnoreCase("Caribbean", pageable)).thenReturn(portPage);
        when(portMapper.toSummaryDto(port)).thenReturn(summary);
        when(portRepository.countToursByPortCode("MIAMI")).thenReturn(0L);

        Page<PortSummaryDto> result = portService.getAllPorts("Caribbean", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).region()).isEqualTo("Caribbean");
    }
}
