package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.*;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.entities.CampaignsEntity;
import com.FedericoFunes.app_service.entities.DonorEntity;
import com.FedericoFunes.app_service.entities.OrganizerEmpEntity;
import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.entities.enums.Gender;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.CampaignsRepository;
import com.FedericoFunes.app_service.services.external.EmailService;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import com.FedericoFunes.app_service.services.impl.CampaignsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignsServiceImplTest {

    @Mock private CampaignsRepository campaignsRepository;
    @Mock private OrganizerEmpService organizerEmpService;
    @Mock private OrganizerPerService organizerPerService;
    @Mock private GoogleMapsService googleMapsService;
    @Mock private DonorService donorService;
    @Mock private EmailService emailService;

    @InjectMocks
    private CampaignsServiceImpl campaignsService;

    private CampaignsEntity activeCampaign;
    private CampaignsEntity finishedCampaign;
    private CampaignsEntity inactiveCampaign;
    private ResponseOrganizerEmpDTO organizerEmpDto;
    private DonorEntity donorEntity;
    private ResponseDonorDTO donorDto;

    private CampaignsEntity buildCampaign(Long id, String title, boolean active, LocalDate endDate, List<DonorEntity> donors) {
        CampaignsEntity c = CampaignsEntity.builder()
                .title(title)
                .description("Desc for " + title)
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(endDate)
                .startTime(LocalTime.of(9, 0))
                .direction("Av. Corrientes 1234")
                .latitude(-34.6037)
                .longitude(-58.3816)
                .bloodFactorRequired(BloodFactor.POSITIVE)
                .bloodGroupRequired(null)
                .subscribedDonors(donors != null ? new ArrayList<>(donors) : new ArrayList<>())
                .wasNotify(false)
                .isActive(active)
                .build();
        c.setId(id);
        return c;
    }

    private DonorEntity buildDonor(Long id, String firstName) {
        DonorEntity d = DonorEntity.builder()
                .firstName(firstName).lastName("Test")
                .birthdate(LocalDate.of(1990, 5, 15)).document("DOC" + id)
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email(firstName.toLowerCase() + "@test.com").phoneNumber("1234567890")
                .isActive(true).build();
        d.setId(id);
        return d;
    }

    @BeforeEach
    void setUp() throws Exception {
        donorEntity = buildDonor(10L, "Juan");
        donorDto = new ResponseDonorDTO();
        donorDto.setId(10L);
        donorDto.setFirstName("Juan");
        donorDto.setLastName("Test");
        donorDto.setBirthdate(LocalDate.of(1990, 5, 15));
        donorDto.setDocument("DOC10");
        donorDto.setBloodFactor(BloodFactor.POSITIVE);
        donorDto.setBloodGroup(BloodGroup.A);
        donorDto.setGender(Gender.MALE);
        donorDto.setHeight(1.75);
        donorDto.setWeight(70.0);
        donorDto.setEmail("juan@test.com");
        donorDto.setPhoneNumber("1234567890");
        donorDto.setIsActive(true);

        activeCampaign = buildCampaign(1L, "Campaña Activa", true, null, new ArrayList<>());
        finishedCampaign = buildCampaign(2L, "Campaña Finalizada", true, LocalDate.of(2026, 6, 1), new ArrayList<>());
        inactiveCampaign = buildCampaign(3L, "Campaña Inactiva", false, null, new ArrayList<>());

        organizerEmpDto = new ResponseOrganizerEmpDTO();
        organizerEmpDto.setId(100L);
        organizerEmpDto.setFullName("Acme S.A.");
        organizerEmpDto.setDocument("30123456");
        organizerEmpDto.setDirection("Av. Libertador 5678");
        organizerEmpDto.setLatitude(-34.5);
        organizerEmpDto.setLongitude(-58.4);
        organizerEmpDto.setEmail("acme@corp.com");
        organizerEmpDto.setPhoneNumber("1122334455");
        organizerEmpDto.setIsActive(true);

        lenient().when(googleMapsService.getLatLngFromAddress(anyString()))
                .thenReturn(new double[]{-34.6037, -58.3816});
        lenient().when(organizerEmpService.GetOrganizerEmpById(100L)).thenReturn(organizerEmpDto);
        lenient().when(donorService.GetAllDonors()).thenReturn(Collections.emptyList());
        lenient().doNothing().when(emailService).notifyCreateCampaign(any(), anyList());
        lenient().doNothing().when(emailService).notifyUpdateCampaign(any(), anyList());
    }

    // ── getAllCampaigns ──

    @Test
    void getAllCampaigns_shouldReturnOnlyActiveAndNonFinished() {
        when(campaignsRepository.findAll()).thenReturn(Arrays.asList(activeCampaign, finishedCampaign, inactiveCampaign));

        var result = campaignsService.getAllCampaigns();

        assertEquals(1, result.size());
        assertEquals("Campaña Activa", result.get(0).getTitle());
    }

    @Test
    void getAllCampaigns_shouldReturnEmpty_whenNoneMatch() {
        when(campaignsRepository.findAll()).thenReturn(Arrays.asList(finishedCampaign, inactiveCampaign));

        var result = campaignsService.getAllCampaigns();

        assertTrue(result.isEmpty());
    }

    // ── getCampaignById ──

    @Test
    void getCampaignById_shouldReturnDto_whenActiveAndNotFinished() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));

        var result = campaignsService.getCampaignById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Campaña Activa", result.getTitle());
    }

    @Test
    void getCampaignById_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.getCampaignById(99L));
    }

    @Test
    void getCampaignById_shouldThrowNotFound_whenInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(NotFoundException.class, () -> campaignsService.getCampaignById(3L));
    }

    @Test
    void getCampaignById_shouldThrowNotFound_whenFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(NotFoundException.class, () -> campaignsService.getCampaignById(2L));
    }

    // ── createCampaign ──

    @Test
    void createCampaign_shouldReturnCreatedDto() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Nueva Campaña");
        request.setDescription("Descripción");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av. Corrientes 1234");
        request.setOrganizerId(100L);

        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(activeCampaign);

        var result = campaignsService.createCampaign(request);

        assertNotNull(result);
        verify(campaignsRepository, times(1)).save(any(CampaignsEntity.class));
        verify(emailService, times(1)).notifyCreateCampaign(any(), anyList());
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenTitleIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle(null);
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenDirectionIsBlank() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("");
        request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenOrganizerIdIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(null);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    // ── updateCampaign ──

    @Test
    void updateCampaign_shouldReturnUpdatedDto() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(activeCampaign);

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Actualizada");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av. Corrientes 1234");
        request.setOrganizerId(100L);

        var result = campaignsService.updateCampaign(request, 1L);

        assertNotNull(result);
        verify(campaignsRepository, times(1)).save(any(CampaignsEntity.class));
        verify(emailService, times(1)).notifyUpdateCampaign(any(), anyList());
    }

    @Test
    void updateCampaign_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(100L);

        assertThrows(NotFoundException.class, () -> campaignsService.updateCampaign(request, 99L));
    }

    @Test
    void updateCampaign_shouldThrowBadRequest_whenInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.updateCampaign(request, 3L));
    }

    @Test
    void updateCampaign_shouldThrowBadRequest_whenFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.updateCampaign(request, 2L));
    }

    // ── deleteCampaign ──

    @Test
    void deleteCampaign_shouldSoftDelete() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        CampaignsEntity deleted = buildCampaign(1L, "Campaña Activa", false, null, new ArrayList<>());
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(deleted);

        var result = campaignsService.deleteCampaign(1L);

        assertNotNull(result);
        verify(campaignsRepository, times(1)).save(any(CampaignsEntity.class));
    }

    @Test
    void deleteCampaign_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.deleteCampaign(99L));
    }

    @Test
    void deleteCampaign_shouldThrowBadRequest_whenFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.deleteCampaign(2L));
    }

    // ── subscribeDonor ──

    @Test
    void subscribeDonor_shouldAddDonor() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        when(donorService.GetDonorById(10L)).thenReturn(donorDto);
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(activeCampaign);

        var result = campaignsService.subscribeDonor(1L, 10L);

        assertNotNull(result);
        verify(campaignsRepository, times(1)).save(any(CampaignsEntity.class));
    }

    @Test
    void subscribeDonor_shouldThrowNotFound_whenCampaignNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.subscribeDonor(99L, 10L));
    }

    @Test
    void subscribeDonor_shouldThrowBadRequest_whenCampaignInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.subscribeDonor(3L, 10L));
    }

    @Test
    void subscribeDonor_shouldThrowBadRequest_whenCampaignFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.subscribeDonor(2L, 10L));
    }

    @Test
    void subscribeDonor_shouldThrowBadRequest_whenAlreadySubscribed() {
        CampaignsEntity withDonor = buildCampaign(1L, "Test", true, null, new ArrayList<>(List.of(donorEntity)));
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(withDonor));
        when(donorService.GetDonorById(10L)).thenReturn(donorDto);

        assertThrows(BadRequestException.class, () -> campaignsService.subscribeDonor(1L, 10L));
    }

    // ── unsubscribeDonor ──

    @Test
    void unsubscribeDonor_shouldRemoveDonor() {
        CampaignsEntity withDonor = buildCampaign(1L, "Test", true, null, new ArrayList<>(List.of(donorEntity)));
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(withDonor));
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(withDonor);

        var result = campaignsService.unsubscribeDonor(1L, 10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void unsubscribeDonor_shouldThrowNotFound_whenCampaignNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.unsubscribeDonor(99L, 10L));
    }

    @Test
    void unsubscribeDonor_shouldThrowBadRequest_whenCampaignInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.unsubscribeDonor(3L, 10L));
    }

    @Test
    void unsubscribeDonor_shouldThrowBadRequest_whenCampaignFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.unsubscribeDonor(2L, 10L));
    }

    @Test
    void unsubscribeDonor_shouldThrowBadRequest_whenNotSubscribed() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.unsubscribeDonor(1L, 99L));
    }

    // ── finishCampaign ──

    @Test
    void finishCampaign_shouldSetEndDate() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(finishedCampaign);

        var result = campaignsService.finishCampaign(1L, LocalDate.of(2026, 8, 15));

        assertNotNull(result);
        verify(campaignsRepository, times(1)).save(any(CampaignsEntity.class));
    }

    @Test
    void finishCampaign_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.finishCampaign(99L, LocalDate.now()));
    }

    @Test
    void finishCampaign_shouldThrowBadRequest_whenInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.finishCampaign(3L, LocalDate.now()));
    }

    @Test
    void finishCampaign_shouldThrowBadRequest_whenAlreadyFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.finishCampaign(2L, LocalDate.now()));
    }

    // ── getAllFinishedCampaigns ──

    @Test
    void getAllFinishedCampaigns_shouldReturnOnlyFinished() {
        when(campaignsRepository.findAll()).thenReturn(Arrays.asList(activeCampaign, finishedCampaign, inactiveCampaign));

        var result = campaignsService.getAllFinishedCampaigns();

        assertEquals(1, result.size());
        assertEquals("Campaña Finalizada", result.get(0).getTitle());
    }

    @Test
    void getAllFinishedCampaigns_shouldReturnEmpty_whenNoneFinished() {
        when(campaignsRepository.findAll()).thenReturn(Arrays.asList(activeCampaign, inactiveCampaign));

        var result = campaignsService.getAllFinishedCampaigns();

        assertTrue(result.isEmpty());
    }

    // ── getSubscribedDonors ──

    @Test
    void getSubscribedDonors_shouldReturnList() {
        CampaignsEntity withDonor = buildCampaign(1L, "Test", true, null, new ArrayList<>(List.of(donorEntity)));
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(withDonor));

        var result = campaignsService.getSubscribedDonors(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
    }

    @Test
    void getSubscribedDonors_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> campaignsService.getSubscribedDonors(99L));
    }

    @Test
    void getSubscribedDonors_shouldThrowBadRequest_whenInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.getSubscribedDonors(3L));
    }

    @Test
    void getSubscribedDonors_shouldThrowBadRequest_whenFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(BadRequestException.class, () -> campaignsService.getSubscribedDonors(2L));
    }

    // ── getBloodEstimatedPerCampaign ──

    @Test
    void getBloodEstimatedPerCampaign_shouldReturnList() {
        Object[] activeRow = new Object[]{1L, "Campaña Activa", 5L};
        Object[] finishedRow = new Object[]{2L, "Campaña Finalizada", 3L};

        when(campaignsRepository.countSubscribersPerActiveCampaign()).thenReturn(Collections.singletonList(activeRow));
        when(campaignsRepository.countSubscribersPerFinishedCampaign()).thenReturn(Collections.singletonList(finishedRow));

        var result = campaignsService.getBloodEstimatedPerCampaign();

        assertEquals(2, result.size());
        assertEquals("ACTIVA", result.get(0).getStatus());
        assertEquals(5L, result.get(0).getSubscribedDonors());
        assertEquals(2250.0, result.get(0).getEstimatedMl(), 0.01);
        assertEquals("FINALIZADA", result.get(1).getStatus());
    }

    // ── getTotalBloodEstimated ──

    @Test
    void getTotalBloodEstimated_shouldReturnTotals() {
        when(campaignsRepository.countTotalSubscribers()).thenReturn(10L);
        when(campaignsRepository.countTotalCampaigns()).thenReturn(5L);

        var result = campaignsService.getTotalBloodEstimated();

        assertEquals(10L, result.getTotalSubscribers());
        assertEquals(5L, result.getTotalCampaigns());
        assertEquals(4500.0, result.getEstimatedMl(), 0.01);
        assertEquals(4.5, result.getEstimatedLiters(), 0.01);
    }

    // ── getBloodTypeRanking ──

    @Test
    void getBloodTypeRanking_shouldReturnRanking() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        Object[] row = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 5L};
        when(campaignsRepository.countBloodTypesByCampaign(1L)).thenReturn(Collections.singletonList(row));

        var result = campaignsService.getBloodTypeRanking(1L);

        assertEquals(1, result.size());
        assertEquals("A_POSITIVE", result.get(0).getBloodType());
        assertEquals(5L, result.get(0).getCount());
    }

    @Test
    void getBloodTypeRanking_shouldReturnEmpty_whenBloodGroupRequired() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        activeCampaign.setBloodGroupRequired(BloodGroup.A);

        var result = campaignsService.getBloodTypeRanking(1L);

        assertTrue(result.isEmpty());
        activeCampaign.setBloodGroupRequired(null);
    }

    // ── getLivesSavedPerCampaign ──

    @Test
    void getLivesSavedPerCampaign_shouldReturnLivesSaved() {
        Object[] row = new Object[]{1L, "Campaña Activa", 4L};
        when(campaignsRepository.countSubscribersPerActiveCampaign()).thenReturn(Collections.singletonList(row));
        when(campaignsRepository.countSubscribersPerFinishedCampaign()).thenReturn(Collections.emptyList());

        var result = campaignsService.getLivesSavedPerCampaign();

        assertEquals(1, result.size());
        assertEquals(12L, result.get(0).getEstimatedLivesSaved());
    }

    // ── getTotalLivesSaved ──

    @Test
    void getTotalLivesSaved_shouldReturnTotals() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaigns()).thenReturn(8L);
        when(campaignsRepository.countFinishedCampaigns()).thenReturn(3L);

        var result = campaignsService.getTotalLivesSaved();

        assertEquals(8L, result.getTotalSubscribers());
        assertEquals(3L, result.getTotalFinishedCampaigns());
        assertEquals(24L, result.getEstimatedLivesSaved());
    }

    // ── getCampaignsByOrganizer ──

    @Test
    void getCampaignsByOrganizer_shouldReturnList() {
        when(campaignsRepository.findByCreatorId(100L)).thenReturn(Arrays.asList(activeCampaign, finishedCampaign));

        var result = campaignsService.getCampaignsByOrganizer(100L);

        assertEquals(2, result.size());
    }

    @Test
    void getCampaignsByOrganizer_shouldReturnEmpty_whenNone() {
        when(campaignsRepository.findByCreatorId(100L)).thenReturn(Collections.emptyList());

        var result = campaignsService.getCampaignsByOrganizer(100L);

        assertTrue(result.isEmpty());
    }

    // ── Organizer metrics ──

    @Test
    void getTotalBloodByOrganizer_shouldReturnTotals() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(6L);
        when(campaignsRepository.countFinishedCampaignsByOrganizer(100L)).thenReturn(2L);

        var result = campaignsService.getTotalBloodByOrganizer(100L);

        assertEquals(6L, result.getTotalSubscribers());
        assertEquals(2700.0, result.getEstimatedMl(), 0.01);
    }

    @Test
    void getBloodTypePercentageByOrganizer_shouldReturnPercentages() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(10L);
        Object[] row = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 6L};
        when(campaignsRepository.countBloodTypesInFinishedCampaignsByOrganizer(100L)).thenReturn(Collections.singletonList(row));

        var result = campaignsService.getBloodTypePercentageByOrganizer(100L);

        assertEquals(1, result.size());
        assertEquals(60.0, result.get(0).getPercentage(), 0.01);
    }

    @Test
    void getBloodTypePercentageByOrganizer_shouldReturnZero_whenNoSubscribers() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(0L);
        when(campaignsRepository.countBloodTypesInFinishedCampaignsByOrganizer(100L)).thenReturn(Collections.emptyList());

        var result = campaignsService.getBloodTypePercentageByOrganizer(100L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getCampaignCountByOrganizer_shouldReturnCount() {
        when(campaignsRepository.countTotalCampaignsByOrganizer(100L)).thenReturn(5L);

        assertEquals(5L, campaignsService.getCampaignCountByOrganizer(100L));
    }

    @Test
    void getFinishedCampaignCountByOrganizer_shouldReturnCount() {
        when(campaignsRepository.countFinishedCampaignsByOrganizer(100L)).thenReturn(2L);

        assertEquals(2L, campaignsService.getFinishedCampaignCountByOrganizer(100L));
    }

    @Test
    void getTotalDonorsByOrganizer_shouldReturnCount() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(8L);

        assertEquals(8L, campaignsService.getTotalDonorsByOrganizer(100L));
    }

    @Test
    void getAverageDonorsPerCampaign_shouldReturnAverage() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(10L);
        when(campaignsRepository.countFinishedCampaignsByOrganizer(100L)).thenReturn(2L);

        assertEquals(5.0, campaignsService.getAverageDonorsPerCampaign(100L), 0.01);
    }

    @Test
    void getAverageDonorsPerCampaign_shouldReturnZero_whenNoFinished() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(0L);
        when(campaignsRepository.countFinishedCampaignsByOrganizer(100L)).thenReturn(0L);

        assertEquals(0.0, campaignsService.getAverageDonorsPerCampaign(100L), 0.01);
    }

    @Test
    void getLivesSavedByOrganizer_shouldReturnLivesSaved() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(6L);
        when(campaignsRepository.countFinishedCampaignsByOrganizer(100L)).thenReturn(2L);

        var result = campaignsService.getLivesSavedByOrganizer(100L);

        assertEquals(6L, result.getTotalSubscribers());
        assertEquals(18L, result.getEstimatedLivesSaved());
    }

    @Test
    void getGeographicDistributionByOrganizer_shouldReturnList() {
        Object[] row = new Object[]{"Av. Corrientes 1234", -34.6037, -58.3816, "Campaña Test", 1L, true, LocalDate.of(2026, 6, 1)};
        when(campaignsRepository.findCampaignLocationsByOrganizer(100L)).thenReturn(Collections.singletonList(row));

        var result = campaignsService.getGeographicDistributionByOrganizer(100L);

        assertEquals(1, result.size());
        assertEquals("Campaña Test", result.get(0).getTitle());
        assertEquals("Av. Corrientes 1234", result.get(0).getDirection());
        assertTrue(result.get(0).getIsFinished());
    }

    // ── getActiveSubscribedCampaigns ──

    @Test
    void getActiveSubscribedCampaigns_shouldReturnList() {
        when(campaignsRepository.findActiveSubscribedCampaignsByDonor(10L)).thenReturn(Collections.singletonList(activeCampaign));

        var result = campaignsService.getActiveSubscribedCampaigns(10L);

        assertEquals(1, result.size());
        assertEquals("Campaña Activa", result.get(0).getTitle());
    }

    @Test
    void getActiveSubscribedCampaigns_shouldReturnEmpty_whenNone() {
        when(campaignsRepository.findActiveSubscribedCampaignsByDonor(10L)).thenReturn(Collections.emptyList());

        var result = campaignsService.getActiveSubscribedCampaigns(10L);

        assertTrue(result.isEmpty());
    }

    // ── getOrganizerById fallback to Per ──

    @Test
    void createCampaign_shouldFallbackToOrganizerPer_whenEmpFails() {
        when(organizerEmpService.GetOrganizerEmpById(200L)).thenThrow(new NotFoundException("not found"));

        ResponseOrganizerPerDTO perDto = new ResponseOrganizerPerDTO();
        perDto.setId(200L);
        perDto.setFirstName("María");
        perDto.setLastName("López");
        perDto.setBirthdate(LocalDate.of(1985, 8, 20));
        perDto.setDocument("87654321");
        perDto.setDirection("Av. Corrientes 1234");
        perDto.setLatitude(-34.6037);
        perDto.setLongitude(-58.3816);
        perDto.setGender(Gender.FEMALE);
        perDto.setEmail("maria@test.com");
        perDto.setPhoneNumber("0987654321");
        perDto.setIsActive(true);
        when(organizerPerService.GetOrganizerPerById(200L)).thenReturn(perDto);

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test Per");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av. Corrientes 1234");
        request.setOrganizerId(200L);

        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(activeCampaign);

        var result = campaignsService.createCampaign(request);

        assertNotNull(result);
        verify(organizerPerService, times(1)).GetOrganizerPerById(200L);
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenOrganizerNotFound() {
        when(organizerEmpService.GetOrganizerEmpById(999L)).thenThrow(new NotFoundException("not found"));
        when(organizerPerService.GetOrganizerPerById(999L)).thenThrow(new NotFoundException("not found"));

        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av.");
        request.setOrganizerId(999L);

        assertThrows(ResponseStatusException.class, () -> campaignsService.createCampaign(request));
    }

    // ── validateCampaign: individual field null/blank tests ──

    @Test
    void createCampaign_shouldThrowBadRequest_whenDtoIsNull() {
        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(null));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenDescriptionIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test"); request.setDescription(null);
        request.setStartDate(LocalDate.of(2026, 8, 1)); request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av."); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenDescriptionIsBlank() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test"); request.setDescription("  ");
        request.setStartDate(LocalDate.of(2026, 8, 1)); request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av."); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenStartDateIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test"); request.setDescription("Desc");
        request.setStartDate(null); request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av."); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenStartTimeIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test"); request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1)); request.setStartTime(null);
        request.setDirection("Av."); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenDirectionIsNull() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("Test"); request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1)); request.setStartTime(LocalTime.of(9, 0));
        request.setDirection(null); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    @Test
    void createCampaign_shouldThrowBadRequest_whenTitleIsBlank() {
        RequestCampaignsDTO request = new RequestCampaignsDTO();
        request.setTitle("  "); request.setDescription("Desc");
        request.setStartDate(LocalDate.of(2026, 8, 1)); request.setStartTime(LocalTime.of(9, 0));
        request.setDirection("Av."); request.setOrganizerId(100L);

        assertThrows(BadRequestException.class, () -> campaignsService.createCampaign(request));
    }

    // ── getBloodTypeRanking: null values ──

    @Test
    void getBloodTypeRanking_shouldHandleNullBloodValues() {
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(activeCampaign));
        Object[] rowNullGroup = new Object[]{null, BloodFactor.POSITIVE, 3L};
        Object[] rowNullFactor = new Object[]{BloodGroup.O, null, 2L};
        when(campaignsRepository.countBloodTypesByCampaign(1L))
                .thenReturn(Arrays.asList(rowNullGroup, rowNullFactor));

        var result = campaignsService.getBloodTypeRanking(1L);

        assertEquals(2, result.size());
        assertEquals("Sin grupo_POSITIVE", result.get(0).getBloodType());
        assertEquals("O_Sin factor", result.get(1).getBloodType());
    }

    // ── getBloodTypePercentageByOrganizer: null values and zero subscribers ──

    @Test
    void getBloodTypePercentageByOrganizer_shouldHandleNullBloodValues() {
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(10L);
        Object[] rowNullGroup = new Object[]{null, BloodFactor.POSITIVE, 3L};
        Object[] rowNullFactor = new Object[]{BloodGroup.O, null, 2L};
        Object[] rowBothNull = new Object[]{null, null, 1L};
        when(campaignsRepository.countBloodTypesInFinishedCampaignsByOrganizer(100L))
                .thenReturn(Arrays.asList(rowNullGroup, rowNullFactor, rowBothNull));

        var result = campaignsService.getBloodTypePercentageByOrganizer(100L);

        assertEquals(3, result.size());
        assertEquals("Sin grupo_POSITIVE", result.get(0).getBloodType());
        assertEquals("O_Sin factor", result.get(1).getBloodType());
        assertEquals("Sin grupo_Sin factor", result.get(2).getBloodType());
    }

    @Test
    void getBloodTypePercentageByOrganizer_shouldReturnZero_whenTotalSubscribersZero() {
        Object[] row = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 5L};
        when(campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(100L)).thenReturn(0L);
        when(campaignsRepository.countBloodTypesInFinishedCampaignsByOrganizer(100L))
                .thenReturn(Collections.singletonList(row));

        var result = campaignsService.getBloodTypePercentageByOrganizer(100L);

        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getPercentage(), 0.01);
    }

    // ── getLivesSavedPerCampaign: finished campaigns ──

    @Test
    void getLivesSavedPerCampaign_shouldIncludeFinishedCampaigns() {
        Object[] activeRow = new Object[]{1L, "Campaña Activa", 4L};
        Object[] finishedRow = new Object[]{2L, "Campaña Finalizada", 6L};
        when(campaignsRepository.countSubscribersPerActiveCampaign())
                .thenReturn(Collections.singletonList(activeRow));
        when(campaignsRepository.countSubscribersPerFinishedCampaign())
                .thenReturn(Collections.singletonList(finishedRow));

        var result = campaignsService.getLivesSavedPerCampaign();

        assertEquals(2, result.size());
        assertEquals("ACTIVA", result.get(0).getStatus());
        assertEquals(12L, result.get(0).getEstimatedLivesSaved());
        assertEquals("FINALIZADA", result.get(1).getStatus());
        assertEquals(18L, result.get(1).getEstimatedLivesSaved());
    }

    @Test
    void getLivesSavedPerCampaign_shouldReturnEmpty_whenNoData() {
        when(campaignsRepository.countSubscribersPerActiveCampaign()).thenReturn(Collections.emptyList());
        when(campaignsRepository.countSubscribersPerFinishedCampaign()).thenReturn(Collections.emptyList());

        var result = campaignsService.getLivesSavedPerCampaign();

        assertTrue(result.isEmpty());
    }

    // ── unsubscribeDonor: removeIf lambda branches ──

    @Test
    void unsubscribeDonor_shouldSkipDonorsWithNullId() {
        DonorEntity nullIdDonor = buildDonor(null, "NullId");
        CampaignsEntity withNullId = buildCampaign(1L, "Test", true, null, new ArrayList<>(List.of(nullIdDonor)));
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(withNullId));

        assertThrows(BadRequestException.class, () -> campaignsService.unsubscribeDonor(1L, 10L));
    }

    @Test
    void unsubscribeDonor_shouldSkipDonorsWithNonMatchingId() {
        DonorEntity otherDonor = buildDonor(20L, "Other");
        CampaignsEntity withOther = buildCampaign(1L, "Test", true, null, new ArrayList<>(List.of(otherDonor)));
        when(campaignsRepository.findById(1L)).thenReturn(Optional.of(withOther));

        assertThrows(BadRequestException.class, () -> campaignsService.unsubscribeDonor(1L, 10L));
    }

    // ── getGeographicDistributionByOrganizer: null endDate ──

    @Test
    void getGeographicDistributionByOrganizer_shouldSetIsFinishedFalse_whenEndDateNull() {
        Object[] row = new Object[]{"Av. Libertador 1000", -34.5, -58.3, "Campaña Futura", 2L, true, null};
        when(campaignsRepository.findCampaignLocationsByOrganizer(100L)).thenReturn(Collections.singletonList(row));

        var result = campaignsService.getGeographicDistributionByOrganizer(100L);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsFinished());
    }

    // ── getGeographicDistributionByOrganizer: multiple rows ──

    @Test
    void getGeographicDistributionByOrganizer_shouldReturnMultipleRows() {
        Object[] row1 = new Object[]{"Av. A", -34.5, -58.3, "C1", 1L, true, LocalDate.of(2026, 6, 1)};
        Object[] row2 = new Object[]{"Av. B", -34.6, -58.4, "C2", 2L, false, null};
        when(campaignsRepository.findCampaignLocationsByOrganizer(100L))
                .thenReturn(Arrays.asList(row1, row2));

        var result = campaignsService.getGeographicDistributionByOrganizer(100L);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getIsFinished());
        assertFalse(result.get(1).getIsFinished());
    }

    // ── notifyUpcomingCampaign: additional branch tests ──

    @Test
    void notifyUpcomingCampaign_shouldSendEmail_whenWithin3Days() {
        CampaignsEntity upcoming = buildCampaign(10L, "Próxima", true, null, new ArrayList<>(List.of(donorEntity)));
        upcoming.setStartDate(LocalDate.now().plusDays(2));
        upcoming.setWasNotify(false);
        when(campaignsRepository.findById(10L)).thenReturn(Optional.of(upcoming));
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(upcoming);

        campaignsService.notifyUpcomingCampaign(10L);

        verify(emailService, times(1)).sendBulkHtmlEmail(anyList(), anyString(), anyString());
    }

    @Test
    void notifyUpcomingCampaign_shouldNotSendEmail_whenAlreadyNotified() {
        CampaignsEntity notified = buildCampaign(10L, "Notified", true, null, new ArrayList<>(List.of(donorEntity)));
        notified.setWasNotify(true);
        when(campaignsRepository.findById(10L)).thenReturn(Optional.of(notified));

        campaignsService.notifyUpcomingCampaign(10L);

        verify(emailService, never()).sendBulkHtmlEmail(anyList(), anyString(), anyString());
    }

    @Test
    void notifyUpcomingCampaign_shouldNotSendEmail_whenMoreThan3DaysAway() {
        CampaignsEntity farAway = buildCampaign(10L, "Lejana", true, null, new ArrayList<>(List.of(donorEntity)));
        farAway.setStartDate(LocalDate.now().plusDays(10));
        farAway.setWasNotify(false);
        when(campaignsRepository.findById(10L)).thenReturn(Optional.of(farAway));
        when(campaignsRepository.save(any(CampaignsEntity.class))).thenReturn(farAway);

        campaignsService.notifyUpcomingCampaign(10L);

        verify(emailService, never()).sendBulkHtmlEmail(anyList(), anyString(), anyString());
    }

    @Test
    void notifyUpcomingCampaign_shouldThrowNotFound_whenNotExist() {
        when(campaignsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> campaignsService.notifyUpcomingCampaign(99L));
    }

    @Test
    void notifyUpcomingCampaign_shouldThrowNotFound_whenInactive() {
        when(campaignsRepository.findById(3L)).thenReturn(Optional.of(inactiveCampaign));

        assertThrows(ResponseStatusException.class, () -> campaignsService.notifyUpcomingCampaign(3L));
    }

    @Test
    void notifyUpcomingCampaign_shouldThrowNotFound_whenFinished() {
        when(campaignsRepository.findById(2L)).thenReturn(Optional.of(finishedCampaign));

        assertThrows(ResponseStatusException.class, () -> campaignsService.notifyUpcomingCampaign(2L));
    }
}
