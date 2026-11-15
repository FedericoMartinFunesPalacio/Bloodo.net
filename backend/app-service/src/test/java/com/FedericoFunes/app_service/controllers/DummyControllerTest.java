package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import com.FedericoFunes.app_service.services.DummyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DummyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DummyService dummyService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ResponseDummyDTO buildResponse(Long id, String name) {
        return ResponseDummyDTO.builder()
                .id(id).name(name)
                .description("Description " + name)
                .serialNumber("SN-" + id)
                .createdAt(LocalDate.of(2025, 1, 15))
                .build();
    }

    @Test
    void getAllDummys_shouldReturnList() throws Exception {
        when(dummyService.getAllDummys()).thenReturn(List.of(buildResponse(1L, "Alpha"), buildResponse(2L, "Beta")));

        mockMvc.perform(get("/api/v1/dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[0].serial_number").value("SN-1"))
                .andExpect(jsonPath("$[1].name").value("Beta"));
    }

    @Test
    void getAllDummys_shouldReturnEmptyList() throws Exception {
        when(dummyService.getAllDummys()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void getDummyById_shouldReturnDto() throws Exception {
        when(dummyService.getDummyById(1L)).thenReturn(buildResponse(1L, "Alpha"));

        mockMvc.perform(get("/api/v1/dummy/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.serial_number").value("SN-1"));
    }

    @Test
    void createDummy_shouldReturnCreatedDto() throws Exception {
        RequestDummyDTO req = RequestDummyDTO.builder()
                .name("Gamma").description("Desc Gamma")
                .serialNumber("SN-3").createdAt(LocalDate.of(2025, 6, 1))
                .build();
        when(dummyService.createDummy(any(RequestDummyDTO.class))).thenReturn(buildResponse(3L, "Gamma"));

        mockMvc.perform(post("/api/v1/dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gamma"));
    }

    @Test
    void updateDummy_shouldReturnUpdatedDto() throws Exception {
        RequestDummyDTO req = RequestDummyDTO.builder()
                .name("Alpha Updated").description("Updated")
                .serialNumber("SN-1-v2").createdAt(LocalDate.of(2025, 3, 10))
                .build();
        ResponseDummyDTO resp = buildResponse(1L, "Alpha Updated");
        when(dummyService.updateDummy(any(RequestDummyDTO.class), eq(1L))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/dummy/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alpha Updated"));
    }
}
