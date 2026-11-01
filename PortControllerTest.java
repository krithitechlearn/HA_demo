package com.shoreexcursions.app.port.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoreexcursions.app.port.dto.PortRequestDto;
import com.shoreexcursions.app.port.dto.PortResponseDto;
import com.shoreexcursions.app.port.dto.PortSummaryDto;
import com.shoreexcursions.app.port.service.PortService;
import com.shoreexcursions.app.exception.DuplicateResourceException;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc controller tests for PortController.
 *
 * @see US-001, US-002, US-003, US-004, US-009, API-004, API-005, API-006, API-007, API-008
 */
@WebMvcTest(PortController.class)
class PortControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PortService portService;

    private static final String BASE_URL = "/api/v1/ports";

    @Test
    @DisplayName("API-004: getAllPorts_authenticated_returns200")
    @WithMockUser(roles = "VIEWER")
    void getAllPorts_authenticated_returns200() throws Exception {
        PortSummaryDto summary = new PortSummaryDto("MIAMI", "Miami", "Caribbean", 0L);
        when(portService.getAllPorts(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].portCode").value("MIAMI"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("API-004: getAllPorts_unauthenticated_returns401")
    void getAllPorts_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("API-005: getPort_existingCode_returns200")
    @WithMockUser(roles = "VIEWER")
    void getPort_existingCode_returns200() throws Exception {
        PortResponseDto response = new PortResponseDto("MIAMI", "Miami", "Caribbean",
                null, null, null, null, Collections.emptyList(), 0L, null, null, "SYSTEM", "SYSTEM");
        when(portService.getPort("MIAMI")).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/MIAMI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portCode").value("MIAMI"));
    }

    @Test
    @DisplayName("API-005: getPort_nonExistingCode_returns404")
    @WithMockUser(roles = "VIEWER")
    void getPort_nonExistingCode_returns404() throws Exception {
        when(portService.getPort("UNKNOWN")).thenThrow(new ResourceNotFoundException("Port", "UNKNOWN"));

        mockMvc.perform(get(BASE_URL + "/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("API-006: createPort_validRequest_returns201")
    @WithMockUser(roles = "EDITOR")
    void createPort_validRequest_returns201() throws Exception {
        PortRequestDto request = new PortRequestDto("MIAMI", "Miami", "Caribbean", null, null, null, null);
        PortResponseDto response = new PortResponseDto("MIAMI", "Miami", "Caribbean",
                null, null, null, null, Collections.emptyList(), 0L, null, null, "editor", "editor");
        when(portService.createPort(any(), anyString())).thenReturn(response);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.portCode").value("MIAMI"));
    }

    @Test
    @DisplayName("API-006: createPort_missingName_returns400")
    @WithMockUser(roles = "EDITOR")
    void createPort_missingName_returns400() throws Exception {
        PortRequestDto request = new PortRequestDto("MIAMI", "", "Caribbean", null, null, null, null);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("API-006: createPort_viewerRole_returns403")
    @WithMockUser(roles = "VIEWER")
    void createPort_viewerRole_returns403() throws Exception {
        PortRequestDto request = new PortRequestDto("MIAMI", "Miami", "Caribbean", null, null, null, null);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("API-006: createPort_duplicateCode_returns409")
    @WithMockUser(roles = "EDITOR")
    void createPort_duplicateCode_returns409() throws Exception {
        PortRequestDto request = new PortRequestDto("MIAMI", "Miami", "Caribbean", null, null, null, null);
        when(portService.createPort(any(), anyString()))
                .thenThrow(new DuplicateResourceException("Port with code 'MIAMI' already exists"));

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("API-008: deletePort_withTours_returns409")
    @WithMockUser(roles = "DB_ADMIN")
    void deletePort_withTours_returns409() throws Exception {
        when(portService.deletePort(eq("MIAMI"), anyString()))
                .thenThrow(new BusinessRuleViolationException("Cannot delete port with tours"));

        mockMvc.perform(delete(BASE_URL + "/MIAMI").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    @DisplayName("API-008: deletePort_noTours_returns204")
    @WithMockUser(roles = "DB_ADMIN")
    void deletePort_noTours_returns204() throws Exception {
        doNothing().when(portService).deletePort(eq("MIAMI"), anyString());

        mockMvc.perform(delete(BASE_URL + "/MIAMI").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
