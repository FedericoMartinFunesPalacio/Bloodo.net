package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.campaigns.*;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.services.CampaignsService;
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
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CampaignsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CampaignsControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignsService campaignsService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ResponseCampaignsDTO buildCampaign(Long id, String title) {
        ResponseCampaignsDTO dto = new ResponseCampaignsDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setDescription("Description of " + title);
        dto.setStartDate(LocalDate.of(2025, 6, 1));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setDirection("Av. Libertador 1234");
        dto.setLatitude(-34.6);
        dto.setLongitude(-58.4);
        dto.setOrganizerId(1L);
        return dto;
    }

    @Test
    void getAllCampaigns_shouldReturnList() throws Exception {
        when(campaignsService.getAllCampaigns())
                .thenReturn(List.of(buildCampaign(1L, "Campaña A"), buildCampaign(2L, "Campaña B")));

        mockMvc.perform(get("/api/v1/campaigns/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Campaña A"));
    }

    @Test
    void getCampaignById_shouldReturnDto() throws Exception {
        when(campaignsService.getCampaignById(1L)).thenReturn(buildCampaign(1L, "Campaña A"));

        mockMvc.perform(get("/api/v1/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Campaña A"));
    }

    @Test
    void getCampaignById_shouldReturn404_whenNotFound() throws Exception {
        when(campaignsService.getCampaignById(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/campaigns/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCampaignsByOrganizer_shouldReturnList() throws Exception {
        when(campaignsService.getCampaignsByOrganizer(1L))
                .thenReturn(List.of(buildCampaign(1L, "Campaña Org1")));

        mockMvc.perform(get("/api/v1/campaigns/organizer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Campaña Org1"));
    }

    @Test
    void createCampaign_shouldReturnCreatedDto() throws Exception {
        RequestCampaignsDTO req = new RequestCampaignsDTO();
        req.setTitle("Nueva Campaña"); req.setDescription("Desc");
        req.setStartDate(LocalDate.of(2025, 7, 1)); req.setStartTime(LocalTime.of(10, 0));
        req.setDirection("Calle Falsa 123"); req.setOrganizerId(1L);

        when(campaignsService.createCampaign(any(RequestCampaignsDTO.class)))
                .thenReturn(buildCampaign(1L, "Nueva Campaña"));

        mockMvc.perform(post("/api/v1/campaigns/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nueva Campaña"));
    }

    @Test
    void updateCampaign_shouldReturnUpdatedDto() throws Exception {
        RequestCampaignsDTO req = new RequestCampaignsDTO();
        req.setTitle("Campaña Actualizada"); req.setDescription("Updated");
        req.setStartDate(LocalDate.of(2025, 7, 1)); req.setStartTime(LocalTime.of(10, 0));
        req.setDirection("Av. Principal 456"); req.setOrganizerId(1L);

        when(campaignsService.updateCampaign(any(RequestCampaignsDTO.class), eq(1L)))
                .thenReturn(buildCampaign(1L, "Campaña Actualizada"));

        mockMvc.perform(put("/api/v1/campaigns/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Campaña Actualizada"));
    }

    @Test
    void deleteCampaign_shouldReturnDeleted() throws Exception {
        ResponseCampaignsDTO resp = buildCampaign(1L, "Campaña A");
        when(campaignsService.deleteCampaign(1L)).thenReturn(resp);

        mockMvc.perform(delete("/api/v1/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Campaña A"));
    }

    @Test
    void subscribeDonor_shouldReturnCampaign() throws Exception {
        when(usersService.getCurrentDonorId()).thenReturn(10L);
        ResponseCampaignsDTO resp = buildCampaign(1L, "Campaña A");
        when(campaignsService.subscribeDonor(1L, 10L)).thenReturn(resp);

        mockMvc.perform(post("/api/v1/campaigns/1/subscribe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Campaña A"));
    }

    @Test
    void subscribeDonor_shouldReturn404_whenNotFound() throws Exception {
        when(usersService.getCurrentDonorId()).thenReturn(10L);
        when(campaignsService.subscribeDonor(99L, 10L))
                .thenThrow(new NotFoundException("Campaign not found"));

        mockMvc.perform(post("/api/v1/campaigns/99/subscribe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unsubscribeDonor_shouldReturnList() throws Exception {
        when(usersService.getCurrentDonorId()).thenReturn(10L);
        SubscribedDonorDTO donor = new SubscribedDonorDTO();
        donor.setId(10L); donor.setFirstName("Juan"); donor.setLastName("Pérez");
        donor.setEmail("juan@test.com"); donor.setDocument("12345678");
        donor.setPhoneNumber("1234567890"); donor.setBloodGroup(BloodGroup.A);
        donor.setBloodFactor(BloodFactor.POSITIVE); donor.setIsActive(true);

        when(campaignsService.unsubscribeDonor(1L, 10L)).thenReturn(List.of(donor));

        mockMvc.perform(delete("/api/v1/campaigns/1/unsubscribe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].first_name").value("Juan"));
    }

    @Test
    void finishCampaign_shouldReturnCampaign() throws Exception {
        ResponseCampaignsDTO resp = buildCampaign(1L, "Campaña A");
        resp.setEndDate(LocalDate.of(2025, 8, 1));
        when(campaignsService.finishCampaign(eq(1L), any(LocalDate.class))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/campaigns/1/finish")
                        .param("endDate", "2025-08-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.end_date").value("01-08-2025"));
    }

    @Test
    void getAllFinishedCampaigns_shouldReturnList() throws Exception {
        when(campaignsService.getAllFinishedCampaigns())
                .thenReturn(List.of(buildCampaign(1L, "Campaña Finalizada")));

        mockMvc.perform(get("/api/v1/campaigns/finished"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Campaña Finalizada"));
    }

    @Test
    void getSubscribedDonors_shouldReturnList() throws Exception {
        SubscribedDonorDTO donor = new SubscribedDonorDTO();
        donor.setId(10L); donor.setFirstName("María"); donor.setLastName("García");
        donor.setEmail("maria@test.com"); donor.setDocument("87654321");
        donor.setPhoneNumber("0987654321"); donor.setBloodGroup(BloodGroup.O);
        donor.setBloodFactor(BloodFactor.NEGATIVE); donor.setIsActive(true);

        when(campaignsService.getSubscribedDonors(1L)).thenReturn(List.of(donor));

        mockMvc.perform(get("/api/v1/campaigns/subscribed/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first_name").value("María"));
    }

    @Test
    void getActiveSubscribedCampaigns_shouldReturnList() throws Exception {
        when(usersService.getCurrentDonorId()).thenReturn(10L);
        when(campaignsService.getActiveSubscribedCampaigns(10L))
                .thenReturn(List.of(buildCampaign(1L, "Active Campaign")));

        mockMvc.perform(get("/api/v1/campaigns/subscribed-by/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Active Campaign"));
    }

    @Test
    void notifyUpcoming_shouldReturn200() throws Exception {
        doNothing().when(campaignsService).notifyUpcomingCampaign(1L);

        mockMvc.perform(post("/api/v1/campaigns/1/notify-upcoming"))
                .andExpect(status().isOk());
    }

    @Test
    void getBloodEstimatedPerCampaign_shouldReturnList() throws Exception {
        BloodEstimatedDTO dto = new BloodEstimatedDTO(1L, "Campaña A", "ACTIVE", 5L, 2250.0, 2.25);
        when(campaignsService.getBloodEstimatedPerCampaign()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/campaigns/metrics/blood-estimated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estimated_ml").value(2250.0));
    }

    @Test
    void getTotalBloodEstimated_shouldReturnDto() throws Exception {
        TotalBloodEstimatedDTO dto = new TotalBloodEstimatedDTO(10L, 3L, 4500.0, 4.5);
        when(campaignsService.getTotalBloodEstimated()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/campaigns/metrics/blood-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimated_liters").value(4.5));
    }

    @Test
    void getBloodTypeRankingPerCampaign_shouldReturnList() throws Exception {
        BloodTypeRankingDTO dto = new BloodTypeRankingDTO("A_POSITIVE", 8L);
        when(campaignsService.getBloodTypeRanking(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/campaigns/metrics/blood-type-ranking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].count").value(8));
    }

    @Test
    void getLivesSavedPerCampaign_shouldReturnList() throws Exception {
        LivesSavedDTO dto = new LivesSavedDTO(1L, "Campaña A", "ACTIVE", 5L, 15L);
        when(campaignsService.getLivesSavedPerCampaign()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/campaigns/metrics/lives-saved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estimated_lives_saved").value(15));
    }

    @Test
    void getTotalLivesSaved_shouldReturnDto() throws Exception {
        TotalLivesSavedDTO dto = new TotalLivesSavedDTO(15L, 2L, 45L);
        when(campaignsService.getTotalLivesSaved()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/campaigns/metrics/lives-saved-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimated_lives_saved").value(45));
    }

    @Test
    void getTotalBloodByOrganizer_shouldReturnDto() throws Exception {
        TotalBloodEstimatedDTO dto = new TotalBloodEstimatedDTO(8L, 2L, 3600.0, 3.6);
        when(campaignsService.getTotalBloodByOrganizer(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/blood-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimated_liters").value(3.6));
    }

    @Test
    void getBloodTypePercentageByOrganizer_shouldReturnList() throws Exception {
        BloodTypePercentageDTO dto = new BloodTypePercentageDTO("A_POSITIVE", 5L, 62.5);
        when(campaignsService.getBloodTypePercentageByOrganizer(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/blood-type-percentage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentage").value(62.5));
    }

    @Test
    void getCampaignCountByOrganizer_shouldReturnCount() throws Exception {
        when(campaignsService.getCampaignCountByOrganizer(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/campaign-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getFinishedCampaignCountByOrganizer_shouldReturnCount() throws Exception {
        when(campaignsService.getFinishedCampaignCountByOrganizer(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/finished-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getTotalDonorsByOrganizer_shouldReturnCount() throws Exception {
        when(campaignsService.getTotalDonorsByOrganizer(1L)).thenReturn(15L);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/total-donors"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void getAverageDonorsPerCampaign_shouldReturnAverage() throws Exception {
        when(campaignsService.getAverageDonorsPerCampaign(1L)).thenReturn(7.5);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/average-donors"))
                .andExpect(status().isOk())
                .andExpect(content().string("7.5"));
    }

    @Test
    void getLivesSavedByOrganizer_shouldReturnDto() throws Exception {
        TotalLivesSavedDTO dto = new TotalLivesSavedDTO(12L, 2L, 36L);
        when(campaignsService.getLivesSavedByOrganizer(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/lives-saved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimated_lives_saved").value(36));
    }

    @Test
    void getGeographicDistributionByOrganizer_shouldReturnList() throws Exception {
        GeographicDistributionDTO dto = new GeographicDistributionDTO(1L, "Campaña A", "Av. Principal", -34.6, -58.4, true, false);
        when(campaignsService.getGeographicDistributionByOrganizer(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/campaigns/metrics/organizer/1/geographic-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].latitude").value(-34.6));
    }
}
