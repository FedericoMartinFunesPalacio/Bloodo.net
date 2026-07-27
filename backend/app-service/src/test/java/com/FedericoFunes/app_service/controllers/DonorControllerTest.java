package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorHealthDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorStatsDTO;
import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.entities.enums.Gender;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.services.DonorService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DonorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DonorControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonorService donorService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ResponseDonorDTO buildResponse(Long id, String firstName) {
        ResponseDonorDTO dto = new ResponseDonorDTO();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName("Test");
        dto.setBirthdate(LocalDate.of(1990, 5, 15));
        dto.setDocument("12345678");
        dto.setBloodFactor(BloodFactor.POSITIVE);
        dto.setBloodGroup(BloodGroup.A);
        dto.setGender(Gender.MALE);
        dto.setHeight(1.75);
        dto.setWeight(70.0);
        dto.setEmail(firstName.toLowerCase() + "@test.com");
        dto.setPhoneNumber("1234567890");
        dto.setIsActive(true);
        return dto;
    }

    @Test
    void getAllDonors_shouldReturnList() throws Exception {
        List<ResponseDonorDTO> list = Arrays.asList(buildResponse(1L, "Juan"), buildResponse(2L, "María"));
        when(donorService.GetAllDonors()).thenReturn(list);

        mockMvc.perform(get("/api/v1/donors/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].first_name").value("Juan"))
                .andExpect(jsonPath("$[1].first_name").value("María"));
    }

    @Test
    void getAllDonors_shouldReturnEmptyList() throws Exception {
        when(donorService.GetAllDonors()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/donors/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void getDonorById_shouldReturnDto() throws Exception {
        when(donorService.GetDonorById(1L)).thenReturn(buildResponse(1L, "Juan"));

        mockMvc.perform(get("/api/v1/donors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void getDonorById_shouldReturn404_whenNotFound() throws Exception {
        when(donorService.GetDonorById(99L)).thenThrow(new NotFoundException("Donor not found"));

        mockMvc.perform(get("/api/v1/donors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDonor_shouldReturnCreatedDto() throws Exception {
        RequestDonorDTO req = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890")
                .build();

        when(donorService.CreateDonor(any(RequestDonorDTO.class))).thenReturn(buildResponse(1L, "Juan"));

        mockMvc.perform(post("/api/v1/donors/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Juan"));
    }

    @Test
    void createDonor_shouldReturn400_whenValidationFails() throws Exception {
        when(donorService.CreateDonor(any(RequestDonorDTO.class)))
                .thenThrow(new BadRequestException("Validation failed"));

        RequestDonorDTO req = RequestDonorDTO.builder()
                .firstName(null).lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/donors/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDonor_shouldReturnUpdatedDto() throws Exception {
        RequestDonorDTO req = RequestDonorDTO.builder()
                .firstName("Carlos").lastName("García")
                .birthdate(LocalDate.of(1988, 3, 20)).document("87654321")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.B)
                .gender(Gender.MALE).height(1.80).weight(80.0)
                .email("carlos@test.com").phoneNumber("0987654321")
                .build();

        ResponseDonorDTO resp = buildResponse(1L, "Carlos");
        when(donorService.UpdateDonor(any(RequestDonorDTO.class), eq(1L))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/donors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Carlos"));
    }

    @Test
    void updateDonor_shouldReturn404_whenNotFound() throws Exception {
        RequestDonorDTO req = RequestDonorDTO.builder()
                .firstName("Test").lastName("Test")
                .birthdate(LocalDate.of(1990, 1, 1)).document("00000000")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.70).weight(70.0)
                .email("test@test.com").phoneNumber("0000000000")
                .build();

        when(donorService.UpdateDonor(any(RequestDonorDTO.class), eq(99L)))
                .thenThrow(new NotFoundException("Donor not found"));

        mockMvc.perform(put("/api/v1/donors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDonor_shouldReturnDeletedDto() throws Exception {
        ResponseDonorDTO resp = buildResponse(1L, "Juan");
        resp.setIsActive(false);
        when(donorService.DeleteDonor(1L)).thenReturn(resp);

        mockMvc.perform(delete("/api/v1/donors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_active").value(false));
    }

    @Test
    void deleteDonor_shouldReturn404_whenNotFound() throws Exception {
        when(donorService.DeleteDonor(99L)).thenThrow(new NotFoundException("Donor not found"));

        mockMvc.perform(delete("/api/v1/donors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDonorStats_shouldReturnStats() throws Exception {
        DonorStatsDTO stats = new DonorStatsDTO(5L, 2250.0, 2.25);
        when(donorService.GetDonorStats(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/donors/1/metrics/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns_attended").value(5))
                .andExpect(jsonPath("$.estimated_ml").value(2250.0));
    }

    @Test
    void getDonorStats_shouldReturn404_whenNotFound() throws Exception {
        when(donorService.GetDonorStats(99L)).thenThrow(new NotFoundException("Donor not found"));

        mockMvc.perform(get("/api/v1/donors/99/metrics/stats"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDonorHealth_shouldReturnHealth() throws Exception {
        DonorHealthDTO health = new DonorHealthDTO("A_POSITIVE", "01-06-2025", "01-09-2025", 22.86, 36);
        when(donorService.GetDonorHealth(1L)).thenReturn(health);

        mockMvc.perform(get("/api/v1/donors/1/metrics/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blood_type").value("A_POSITIVE"))
                .andExpect(jsonPath("$.age").value(36));
    }

    @Test
    void getDonorHealth_shouldReturn404_whenNotFound() throws Exception {
        when(donorService.GetDonorHealth(99L)).thenThrow(new NotFoundException("Donor not found"));

        mockMvc.perform(get("/api/v1/donors/99/metrics/health"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBloodTypeRanking_shouldReturnList() throws Exception {
        BloodTypeRankingDTO r1 = new BloodTypeRankingDTO("A_POSITIVE", 10L);
        BloodTypeRankingDTO r2 = new BloodTypeRankingDTO("O_NEGATIVE", 5L);
        when(donorService.GetBloodTypeRanking()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/v1/donors/metrics/blood-type-ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].blood_type").value("A_POSITIVE"))
                .andExpect(jsonPath("$[1].count").value(5));
    }

    @Test
    void getBloodTypePercentage_shouldReturnList() throws Exception {
        BloodTypePercentageDTO p1 = new BloodTypePercentageDTO("A_POSITIVE", 10L, 60.0);
        when(donorService.GetBloodTypePercentage()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/donors/metrics/blood-type-percentage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentage").value(60.0));
    }
}
