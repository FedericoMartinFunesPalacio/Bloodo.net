package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.organizerper.RequestOrganizerPerDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.entities.enums.Gender;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.services.OrganizerPerService;
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

@WebMvcTest(controllers = OrganizerPerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganizerPerControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizerPerService organizerPerService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ResponseOrganizerPerDTO buildResponse(Long id, String firstName) {
        return ResponseOrganizerPerDTO.builder()
                .id(id).firstName(firstName).lastName("Test")
                .birthdate(LocalDate.of(1985, 7, 20)).document("87654321")
                .direction("Calle 123").latitude(-34.6).longitude(-58.4)
                .gender(Gender.MALE).email(firstName.toLowerCase() + "@org.com")
                .phoneNumber("1122334455").isActive(true)
                .build();
    }

    @Test
    void getAllOrganizerPers_shouldReturnList() throws Exception {
        when(organizerPerService.GetAllOrganizerPers())
                .thenReturn(List.of(buildResponse(1L, "María"), buildResponse(2L, "Carlos")));

        mockMvc.perform(get("/api/v1/organizer-pers/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].first_name").value("María"))
                .andExpect(jsonPath("$[1].first_name").value("Carlos"));
    }

    @Test
    void getAllOrganizerPers_shouldReturnEmptyList() throws Exception {
        when(organizerPerService.GetAllOrganizerPers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/organizer-pers/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void getOrganizerPerById_shouldReturnDto() throws Exception {
        when(organizerPerService.GetOrganizerPerById(1L)).thenReturn(buildResponse(1L, "María"));

        mockMvc.perform(get("/api/v1/organizer-pers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("María"));
    }

    @Test
    void getOrganizerPerById_shouldReturn404_whenNotFound() throws Exception {
        when(organizerPerService.GetOrganizerPerById(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/organizer-pers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrganizerPer_shouldReturnCreatedDto() throws Exception {
        RequestOrganizerPerDTO req = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 7, 20)).document("87654321")
                .direction("Calle 123").gender(Gender.FEMALE)
                .email("maria@org.com").phoneNumber("1122334455")
                .build();

        when(organizerPerService.CreateOrganizerPer(any(RequestOrganizerPerDTO.class)))
                .thenReturn(buildResponse(1L, "María"));

        mockMvc.perform(post("/api/v1/organizer-pers/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("María"));
    }

    @Test
    void createOrganizerPer_shouldReturn400_whenInvalid() throws Exception {
        when(organizerPerService.CreateOrganizerPer(any(RequestOrganizerPerDTO.class)))
                .thenThrow(new BadRequestException("Validation failed"));

        RequestOrganizerPerDTO req = RequestOrganizerPerDTO.builder()
                .firstName(null).lastName("López")
                .birthdate(LocalDate.of(1985, 7, 20)).document("87654321")
                .direction("Calle 123").gender(Gender.FEMALE)
                .email("maria@org.com").phoneNumber("1122334455")
                .build();

        mockMvc.perform(post("/api/v1/organizer-pers/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrganizerPer_shouldReturnUpdatedDto() throws Exception {
        RequestOrganizerPerDTO req = RequestOrganizerPerDTO.builder()
                .firstName("María Updated").lastName("López")
                .birthdate(LocalDate.of(1985, 7, 20)).document("87654321")
                .direction("Av. Principal 456").gender(Gender.FEMALE)
                .email("maria@org.com").phoneNumber("1122334455")
                .build();

        ResponseOrganizerPerDTO resp = buildResponse(1L, "María Updated");
        when(organizerPerService.UpdateOrganizerPer(any(RequestOrganizerPerDTO.class), eq(1L)))
                .thenReturn(resp);

        mockMvc.perform(put("/api/v1/organizer-pers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("María Updated"));
    }

    @Test
    void updateOrganizerPer_shouldReturn404_whenNotFound() throws Exception {
        RequestOrganizerPerDTO req = RequestOrganizerPerDTO.builder()
                .firstName("Test").lastName("Test")
                .birthdate(LocalDate.of(1990, 1, 1)).document("00000000")
                .direction("Test").gender(Gender.MALE)
                .email("test@test.com").phoneNumber("0000000000")
                .build();

        when(organizerPerService.UpdateOrganizerPer(any(RequestOrganizerPerDTO.class), eq(99L)))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/organizer-pers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrganizerPer_shouldReturnDeletedDto() throws Exception {
        ResponseOrganizerPerDTO resp = buildResponse(1L, "María");
        resp.setIsActive(false);
        when(organizerPerService.DeleteOrganizerPer(1L)).thenReturn(resp);

        mockMvc.perform(delete("/api/v1/organizer-pers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_active").value(false));
    }

    @Test
    void deleteOrganizerPer_shouldReturn404_whenNotFound() throws Exception {
        when(organizerPerService.DeleteOrganizerPer(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(delete("/api/v1/organizer-pers/99"))
                .andExpect(status().isNotFound());
    }
}
