package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.organizeremp.RequestOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.services.OrganizerEmpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrganizerEmpController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganizerEmpControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizerEmpService organizerEmpService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseOrganizerEmpDTO buildResponse(Long id, String fullName) {
        return ResponseOrganizerEmpDTO.builder()
                .id(id).fullName(fullName).document("11223344")
                .direction("Av. Corrientes 1234").latitude(-34.6).longitude(-58.4)
                .email(fullName.toLowerCase().replace(" ", "") + "@emp.com")
                .phoneNumber("1155667788").isActive(true)
                .build();
    }

    @Test
    void getAllOrganizerEmps_shouldReturnList() throws Exception {
        when(organizerEmpService.GetAllOrganizerEmps())
                .thenReturn(List.of(buildResponse(1L, "Tech Corp"), buildResponse(2L, "Health SA")));

        mockMvc.perform(get("/api/v1/organizer-emps/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].full_name").value("Tech Corp"))
                .andExpect(jsonPath("$[1].full_name").value("Health SA"));
    }

    @Test
    void getAllOrganizerEmps_shouldReturnEmptyList() throws Exception {
        when(organizerEmpService.GetAllOrganizerEmps()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/organizer-emps/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void getOrganizerEmpById_shouldReturnDto() throws Exception {
        when(organizerEmpService.GetOrganizerEmpById(1L)).thenReturn(buildResponse(1L, "Tech Corp"));

        mockMvc.perform(get("/api/v1/organizer-emps/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("Tech Corp"));
    }

    @Test
    void getOrganizerEmpById_shouldReturn404_whenNotFound() throws Exception {
        when(organizerEmpService.GetOrganizerEmpById(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/organizer-emps/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrganizerEmp_shouldReturnCreatedDto() throws Exception {
        RequestOrganizerEmpDTO req = RequestOrganizerEmpDTO.builder()
                .fullName("Tech Corp").document("11223344")
                .direction("Av. Corrientes 1234").email("tech@emp.com")
                .phoneNumber("1155667788")
                .build();

        when(organizerEmpService.CreateOrganizerEmp(any(RequestOrganizerEmpDTO.class)))
                .thenReturn(buildResponse(1L, "Tech Corp"));

        mockMvc.perform(post("/api/v1/organizer-emps/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("Tech Corp"));
    }

    @Test
    void createOrganizerEmp_shouldReturn400_whenInvalid() throws Exception {
        when(organizerEmpService.CreateOrganizerEmp(any(RequestOrganizerEmpDTO.class)))
                .thenThrow(new BadRequestException("Validation failed"));

        RequestOrganizerEmpDTO req = RequestOrganizerEmpDTO.builder()
                .fullName(null).document("11223344")
                .direction("Av. Corrientes 1234").email("tech@emp.com")
                .phoneNumber("1155667788")
                .build();

        mockMvc.perform(post("/api/v1/organizer-emps/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrganizerEmp_shouldReturnUpdatedDto() throws Exception {
        RequestOrganizerEmpDTO req = RequestOrganizerEmpDTO.builder()
                .fullName("Tech Corp Updated").document("11223344")
                .direction("Av. Corrientes 5678").email("tech@emp.com")
                .phoneNumber("1155667788")
                .build();

        when(organizerEmpService.UpdateOrganizerEmp(any(RequestOrganizerEmpDTO.class), eq(1L)))
                .thenReturn(buildResponse(1L, "Tech Corp Updated"));

        mockMvc.perform(put("/api/v1/organizer-emps/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("Tech Corp Updated"));
    }

    @Test
    void updateOrganizerEmp_shouldReturn404_whenNotFound() throws Exception {
        RequestOrganizerEmpDTO req = RequestOrganizerEmpDTO.builder()
                .fullName("Test").document("00000000")
                .direction("Test").email("test@test.com")
                .phoneNumber("0000000000")
                .build();

        when(organizerEmpService.UpdateOrganizerEmp(any(RequestOrganizerEmpDTO.class), eq(99L)))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/organizer-emps/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrganizerEmp_shouldReturnDeletedDto() throws Exception {
        ResponseOrganizerEmpDTO resp = buildResponse(1L, "Tech Corp");
        resp.setIsActive(false);
        when(organizerEmpService.DeleteOrganizerEmp(1L)).thenReturn(resp);

        mockMvc.perform(delete("/api/v1/organizer-emps/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_active").value(false));
    }

    @Test
    void deleteOrganizerEmp_shouldReturn404_whenNotFound() throws Exception {
        when(organizerEmpService.DeleteOrganizerEmp(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(delete("/api/v1/organizer-emps/99"))
                .andExpect(status().isNotFound());
    }
}
