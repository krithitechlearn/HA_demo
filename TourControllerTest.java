package com.shoreexcursions.app.tour.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoreexcursions.app.exception.ResourceNotFoundException;
import com.shoreexcursions.app.tour.dto.TourRequestDto;
import com.shoreexcursions.app.tour.dto.TourResponseDto;
import com.shoreexcursions.app.tour.dto.TourSummaryDto;
import com.shoreexcursions.app.tour.service.TourService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc controller tests for TourController.
 *
 * @see US-005, US-006, US-007, US-008, US-010, API-009, API-010, API-011, API-012, API-013, API-014
 */
@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TourService tourService;

    @Test
    @DisplayName("API-009: getAllTours_authenticated_returns200")
    @WithMockUser(roles = "VIEWER")
    void getAllTours_authenticated_returns200() throws Exception {
        UUID tourId = UUID.randomUUID();
        TourSummaryDto summary = new TourSummaryDto(tourId, "MIAMI", "City Tour", "Culture", "LOW", 120, false);
        when(tourService.getAllTours(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/api/v1/tours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tourName").value("City Tour"));
    }

    @Test
    @DisplayName("API-010: getTour_existingId_returns200")
    @WithMockUser(roles = "VIEWER")
    void getTour_existingId_returns200() throws Exception {
        UUID tourId = UUID.randomUUID();
        TourResponseDto response = new TourResponseDto(tourId, "MIAMI", "Miami", "City Tour",
                120, "Culture", null, null, null, false, "LOW", null,
                Collections.emptyList(), null, null, "SYSTEM", "SYSTEM", 0);
        when(tourService.getTour(tourId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tours/" + tourId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tourId").value(tourId.toString()));
    }

    @Test
    @DisplayName("API-010: getTour_nonExistingId_returns404")
    @WithMockUser(roles = "VIEWER")
    void getTour_nonExistingId_returns404() throws Exception {
        UUID tourId = UUID.randomUUID();
        when(tourService.getTour(tourId)).thenThrow(new ResourceNotFoundException("Tour", tourId));

        mockMvc.perform(get("/api/v1/tours/" + tourId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("API-011: createTour_validRequest_returns201")
    @WithMockUser(roles = "EDITOR")
    void createTour_validRequest_returns201() throws Exception {
        UUID tourId = UUID.randomUUID();
        TourRequestDto request = new TourRequestDto("MIAMI", "City Tour", 120, "Culture",
                null, null, null, false, "LOW", null);
        TourResponseDto response = new TourResponseDto(tourId, "MIAMI", "Miami", "City Tour",
                120, "Culture", null, null, null, false, "LOW", null,
                Collections.emptyList(), null, null, "editor", "editor", 0);
        when(tourService.createTour(any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/tours").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tourId").value(tourId.toString()));
    }

    @Test
    @DisplayName("API-011: createTour_missingTourName_returns400")
    @WithMockUser(roles = "EDITOR")
    void createTour_missingTourName_returns400() throws Exception {
        TourRequestDto request = new TourRequestDto("MIAMI", "", null, null,
                null, null, null, false, null, null);

        mockMvc.perform(post("/api/v1/tours").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("API-013: deleteTour_dbAdmin_returns204")
    @WithMockUser(roles = "DB_ADMIN")
    void deleteTour_dbAdmin_returns204() throws Exception {
        UUID tourId = UUID.randomUUID();
        doNothing().when(tourService).deleteTour(eq(tourId), anyString());

        mockMvc.perform(delete("/api/v1/tours/" + tourId).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("API-013: deleteTour_editorRole_returns403")
    @WithMockUser(roles = "EDITOR")
    void deleteTour_editorRole_returns403() throws Exception {
        UUID tourId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/tours/" + tourId).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
