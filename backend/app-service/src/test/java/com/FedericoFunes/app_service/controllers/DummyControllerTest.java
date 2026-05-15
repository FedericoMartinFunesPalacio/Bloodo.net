package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import com.FedericoFunes.app_service.services.DummyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DummyController.class)
public class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DummyService dummyService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void getAllDummys_shouldReturnList() throws Exception {
        ResponseDummyDTO a = ResponseDummyDTO.builder()
                .id(10L)
                .name("John Doe")
                .description("A sample user")
                .serialNumber("SN123")
                .createdAt(LocalDate.of(2025,8,1))
                .build();
        ResponseDummyDTO b = ResponseDummyDTO.builder()
                .id(11L)
                .name("Jane")
                .description("Another")
                .serialNumber("SN456")
                .createdAt(LocalDate.of(2025,8,2))
                .build();
        List<ResponseDummyDTO> list = Arrays.asList(a,b);
        Mockito.when(dummyService.getAllDummys()).thenReturn(list);

        mockMvc.perform(get("/api/v1/dummy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane"));
    }

    @Test
    void getDummyById_shouldReturnDto() throws Exception {
        ResponseDummyDTO a = ResponseDummyDTO.builder()
                .id(10L)
                .name("John Doe")
                .description("A sample user")
                .serialNumber("SN123")
                .createdAt(LocalDate.of(2025,8,1))
                .build();
        Mockito.when(dummyService.getDummyById(10L)).thenReturn(a);

        mockMvc.perform(get("/api/v1/dummy/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.serial_number").value("SN123"));
    }

    @Test
    void getDummyById_shouldReturnServerError_whenServiceThrows() throws Exception {
        Mockito.when(dummyService.getDummyById(99L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/v1/dummy/99")
                                .accept(MediaType.APPLICATION_JSON))
        );
    }

    @Test
    void createDummy_shouldReturnCreatedDto() throws Exception {
        RequestDummyDTO req = RequestDummyDTO.builder()
                .name("John Doe")
                .description("A sample user")
                .serialNumber("SN123")
                .createdAt(LocalDate.of(2025,8,1))
                .build();

        ResponseDummyDTO resp = ResponseDummyDTO.builder()
                .id(10L)
                .name(req.getName())
                .description(req.getDescription())
                .serialNumber(req.getSerialNumber())
                .createdAt(req.getCreatedAt())
                .build();

        Mockito.when(dummyService.createDummy(any(RequestDummyDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.serial_number").value("SN123"));
    }

    @Test
    void updateDummy_shouldReturnUpdatedDto() throws Exception {
        RequestDummyDTO req = RequestDummyDTO.builder()
                .name("Updated")
                .description("Updated desc")
                .serialNumber("SN999")
                .createdAt(LocalDate.of(2025,8,3))
                .build();

        ResponseDummyDTO resp = ResponseDummyDTO.builder()
                .id(10L)
                .name(req.getName())
                .description(req.getDescription())
                .serialNumber(req.getSerialNumber())
                .createdAt(req.getCreatedAt())
                .build();

        Mockito.when(dummyService.updateDummy(any(RequestDummyDTO.class), eq(10L))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/dummy/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.serial_number").value("SN999"));
    }
}
