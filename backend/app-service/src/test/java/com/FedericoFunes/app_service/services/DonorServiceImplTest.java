package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorHealthDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorStatsDTO;
import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.entities.DonorEntity;
import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.entities.enums.Gender;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.DonorRepository;
import com.FedericoFunes.app_service.services.impl.DonorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonorServiceImplTest {

    @Mock
    private DonorRepository donorRepository;

    @InjectMocks
    private DonorServiceImpl donorService;

    private RequestDonorDTO request;
    private DonorEntity entity;

    @BeforeEach
    void setUp() {
        request = RequestDonorDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15))
                .document("12345678")
                .bloodFactor(BloodFactor.POSITIVE)
                .bloodGroup(BloodGroup.A)
                .gender(Gender.MALE)
                .height(1.75)
                .weight(70.0)
                .email("juan@test.com")
                .phoneNumber("1234567890")
                .build();

        entity = DonorEntity.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15))
                .document("12345678")
                .bloodFactor(BloodFactor.POSITIVE)
                .bloodGroup(BloodGroup.A)
                .gender(Gender.MALE)
                .height(1.75)
                .weight(70.0)
                .email("juan@test.com")
                .phoneNumber("1234567890")
                .isActive(true)
                .build();
    }

    // ── GetAllDonors ──

    @Test
    void getAllDonors_shouldReturnOnlyActiveDonors() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findAll()).thenReturn(Arrays.asList(entity, inactive));

        List<ResponseDonorDTO> result = donorService.GetAllDonors();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        verify(donorRepository, times(1)).findAll();
    }

    @Test
    void getAllDonors_shouldReturnEmptyListWhenNoActiveDonors() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findAll()).thenReturn(Collections.singletonList(inactive));

        List<ResponseDonorDTO> result = donorService.GetAllDonors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllDonors_shouldReturnEmptyListWhenRepositoryEmpty() {
        when(donorRepository.findAll()).thenReturn(Collections.emptyList());

        List<ResponseDonorDTO> result = donorService.GetAllDonors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── GetDonorById ──

    @Test
    void getDonorById_shouldReturnDto_whenFoundAndActive() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));

        ResponseDonorDTO result = donorService.GetDonorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Pérez", result.getLastName());
        assertEquals(BloodFactor.POSITIVE, result.getBloodFactor());
        assertEquals(BloodGroup.A, result.getBloodGroup());
        assertTrue(result.getIsActive());
    }

    @Test
    void getDonorById_shouldThrowNotFoundException_whenNotFound() {
        when(donorRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> donorService.GetDonorById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void getDonorById_shouldThrowNotFoundException_whenInactive() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> donorService.GetDonorById(2L));
    }

    // ── CreateDonor ──

    @Test
    void createDonor_shouldReturnCreatedDto() {
        when(donorRepository.save(any(DonorEntity.class))).thenReturn(entity);

        ResponseDonorDTO result = donorService.CreateDonor(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals("Pérez", result.getLastName());
        assertEquals("juan@test.com", result.getEmail());
        assertTrue(result.getIsActive());
        verify(donorRepository, times(1)).save(any(DonorEntity.class));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenFirstNameIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName(null)
                .lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15))
                .document("12345678")
                .bloodFactor(BloodFactor.POSITIVE)
                .bloodGroup(BloodGroup.A)
                .gender(Gender.MALE)
                .height(1.75)
                .weight(70.0)
                .email("juan@test.com")
                .phoneNumber("1234567890")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenAllFieldsNull() {
        RequestDonorDTO invalid = new RequestDonorDTO();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenLastNameIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName(null)
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenBirthdateIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez").birthdate(null)
                .document("12345678").bloodFactor(BloodFactor.POSITIVE)
                .bloodGroup(BloodGroup.A).gender(Gender.MALE)
                .height(1.75).weight(70.0).email("juan@test.com")
                .phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenDocumentIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document(null)
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenBloodFactorIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(null).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenBloodGroupIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(null)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenGenderIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(null).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenHeightIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(null).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenWeightIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(null)
                .email("juan@test.com").phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenEmailIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email(null).phoneNumber("1234567890").build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    @Test
    void createDonor_shouldThrowBadRequest_whenPhoneNumberIsNull() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber(null).build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(invalid));
    }

    // ── UpdateDonor ──

    @Test
    void updateDonor_shouldReturnUpdatedDto() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        DonorEntity updated = DonorEntity.builder()
                .id(1L).firstName("Carlos").lastName("García")
                .birthdate(LocalDate.of(1988, 3, 20)).document("87654321")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.B)
                .gender(Gender.MALE).height(1.80).weight(80.0)
                .email("carlos@test.com").phoneNumber("0987654321")
                .isActive(true).build();
        when(donorRepository.save(any(DonorEntity.class))).thenReturn(updated);

        RequestDonorDTO updateDto = RequestDonorDTO.builder()
                .firstName("Carlos").lastName("García")
                .birthdate(LocalDate.of(1988, 3, 20)).document("87654321")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.B)
                .gender(Gender.MALE).height(1.80).weight(80.0)
                .email("carlos@test.com").phoneNumber("0987654321")
                .build();

        ResponseDonorDTO result = donorService.UpdateDonor(updateDto, 1L);

        assertNotNull(result);
        assertEquals("Carlos", result.getFirstName());
        assertEquals("García", result.getLastName());
        assertEquals(BloodFactor.NEGATIVE, result.getBloodFactor());
        verify(donorRepository, times(1)).findById(1L);
        verify(donorRepository, times(1)).save(any(DonorEntity.class));
    }

    @Test
    void updateDonor_shouldThrowResponseStatus_whenNotFound() {
        when(donorRepository.findById(99L)).thenReturn(Optional.empty());

        RequestDonorDTO updateDto = RequestDonorDTO.builder()
                .firstName("Test").lastName("Test")
                .birthdate(LocalDate.of(1990, 1, 1)).document("00000000")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.70).weight(70.0)
                .email("test@test.com").phoneNumber("0000000000")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.UpdateDonor(updateDto, 99L));
    }

    @Test
    void updateDonor_shouldThrowResponseStatus_whenInactive() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findById(2L)).thenReturn(Optional.of(inactive));

        RequestDonorDTO updateDto = RequestDonorDTO.builder()
                .firstName("Test").lastName("Test")
                .birthdate(LocalDate.of(1990, 1, 1)).document("00000000")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.70).weight(70.0)
                .email("test@test.com").phoneNumber("0000000000")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.UpdateDonor(updateDto, 2L));
    }

    @Test
    void updateDonor_shouldThrowResponseStatus_whenValidationFails() {
        RequestDonorDTO invalid = RequestDonorDTO.builder()
                .firstName(null)
                .lastName("Test")
                .birthdate(LocalDate.of(1990, 1, 1))
                .document("00000000")
                .bloodFactor(BloodFactor.POSITIVE)
                .bloodGroup(BloodGroup.A)
                .gender(Gender.MALE)
                .height(1.70)
                .weight(70.0)
                .email("test@test.com")
                .phoneNumber("0000000000")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.UpdateDonor(invalid, 1L));
    }

    // ── DeleteDonor ──

    @Test
    void deleteDonor_shouldSoftDeleteAndReturnDto() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        DonorEntity deleted = DonorEntity.builder()
                .id(1L).firstName("Juan").lastName("Pérez")
                .birthdate(LocalDate.of(1990, 5, 15)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("juan@test.com").phoneNumber("1234567890")
                .isActive(false).build();
        when(donorRepository.save(any(DonorEntity.class))).thenReturn(deleted);

        ResponseDonorDTO result = donorService.DeleteDonor(1L);

        assertNotNull(result);
        assertFalse(result.getIsActive());
        verify(donorRepository, times(1)).save(any(DonorEntity.class));
    }

    @Test
    void deleteDonor_shouldThrowResponseStatus_whenNotFound() {
        when(donorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> donorService.DeleteDonor(99L));
    }

    @Test
    void deleteDonor_shouldThrowResponseStatus_whenAlreadyInactive() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(ResponseStatusException.class,
                () -> donorService.DeleteDonor(2L));
    }

    // ── GetBloodTypeRanking ──

    @Test
    void getBloodTypeRanking_shouldReturnRankingList() {
        Object[] row1 = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 5L};
        Object[] row2 = new Object[]{BloodGroup.O, BloodFactor.NEGATIVE, 3L};

        when(donorRepository.countBloodTypesGlobally()).thenReturn(Arrays.asList(row1, row2));

        List<BloodTypeRankingDTO> result = donorService.GetBloodTypeRanking();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A_POSITIVE", result.get(0).getBloodType());
        assertEquals(5L, result.get(0).getCount());
        assertEquals("O_NEGATIVE", result.get(1).getBloodType());
        assertEquals(3L, result.get(1).getCount());
    }

    @Test
    void getBloodTypeRanking_shouldReturnEmptyList_whenNoData() {
        when(donorRepository.countBloodTypesGlobally()).thenReturn(Collections.emptyList());

        List<BloodTypeRankingDTO> result = donorService.GetBloodTypeRanking();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBloodTypeRanking_shouldHandleNullValues() {
        Object[] rowWithNulls = new Object[]{null, null, 2L};

        when(donorRepository.countBloodTypesGlobally()).thenReturn(Collections.singletonList(rowWithNulls));

        List<BloodTypeRankingDTO> result = donorService.GetBloodTypeRanking();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sin grupo_Sin factor", result.get(0).getBloodType());
    }

    // ── GetBloodTypePercentage ──

    @Test
    void getBloodTypePercentage_shouldReturnPercentages() {
        Object[] row1 = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 6L};
        Object[] row2 = new Object[]{BloodGroup.O, BloodFactor.NEGATIVE, 4L};

        when(donorRepository.countActiveDonors()).thenReturn(10L);
        when(donorRepository.countBloodTypesGlobally()).thenReturn(Arrays.asList(row1, row2));

        List<BloodTypePercentageDTO> result = donorService.GetBloodTypePercentage();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A_POSITIVE", result.get(0).getBloodType());
        assertEquals(6L, result.get(0).getCount());
        assertEquals(60.0, result.get(0).getPercentage(), 0.01);
        assertEquals("O_NEGATIVE", result.get(1).getBloodType());
        assertEquals(4L, result.get(1).getCount());
        assertEquals(40.0, result.get(1).getPercentage(), 0.01);
    }

    @Test
    void getBloodTypePercentage_shouldReturnZero_whenNoActiveDonors() {
        when(donorRepository.countActiveDonors()).thenReturn(0L);
        when(donorRepository.countBloodTypesGlobally()).thenReturn(Collections.emptyList());

        List<BloodTypePercentageDTO> result = donorService.GetBloodTypePercentage();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── GetDonorStats ──

    @Test
    void getDonorStats_shouldReturnStats() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(donorRepository.countFinishedCampaignsByDonor(1L)).thenReturn(3L);

        DonorStatsDTO result = donorService.GetDonorStats(1L);

        assertNotNull(result);
        assertEquals(3L, result.getCampaignsAttended());
        assertEquals(1350.0, result.getEstimatedMl(), 0.01);
        assertEquals(1.35, result.getEstimatedLiters(), 0.01);
    }

    @Test
    void getDonorStats_shouldReturnZero_whenNoDonations() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(donorRepository.countFinishedCampaignsByDonor(1L)).thenReturn(0L);

        DonorStatsDTO result = donorService.GetDonorStats(1L);

        assertNotNull(result);
        assertEquals(0L, result.getCampaignsAttended());
        assertEquals(0.0, result.getEstimatedMl(), 0.01);
        assertEquals(0.0, result.getEstimatedLiters(), 0.01);
    }

    @Test
    void getDonorStats_shouldThrowNotFound_whenDonorNotExist() {
        when(donorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> donorService.GetDonorStats(99L));
    }

    @Test
    void getDonorStats_shouldThrowNotFound_whenInactive() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> donorService.GetDonorStats(2L));
    }

    // ── GetDonorHealth ──

    @Test
    void getDonorHealth_shouldReturnHealthWithDonationHistory() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        LocalDate lastDonation = LocalDate.of(2025, 6, 1);
        when(donorRepository.findFinishedCampaignEndDatesByDonor(1L))
                .thenReturn(Collections.singletonList(lastDonation));

        DonorHealthDTO result = donorService.GetDonorHealth(1L);

        assertNotNull(result);
        assertEquals("A_POSITIVE", result.getBloodType());
        assertEquals("01-06-2025", result.getLastDonationDate());
        assertEquals("01-09-2025", result.getNextEligibleDate());
        assertNotNull(result.getBmi());
        assertNotNull(result.getAge());
        assertTrue(result.getAge() > 0);
    }

    @Test
    void getDonorHealth_shouldReturnHealthWithoutDonationHistory() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(donorRepository.findFinishedCampaignEndDatesByDonor(1L))
                .thenReturn(Collections.emptyList());

        DonorHealthDTO result = donorService.GetDonorHealth(1L);

        assertNotNull(result);
        assertEquals("A_POSITIVE", result.getBloodType());
        assertNull(result.getLastDonationDate());
        assertNull(result.getNextEligibleDate());
    }

    @Test
    void getDonorHealth_shouldCalculateBmiCorrectly() {
        DonorEntity tallDonor = DonorEntity.builder()
                .id(3L).firstName("Tall").lastName("Donor")
                .birthdate(LocalDate.of(1995, 8, 10)).document("55555555")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.B)
                .gender(Gender.MALE).height(1.90).weight(85.0)
                .email("tall@test.com").phoneNumber("5555555555")
                .isActive(true).build();

        when(donorRepository.findById(3L)).thenReturn(Optional.of(tallDonor));
        when(donorRepository.findFinishedCampaignEndDatesByDonor(3L))
                .thenReturn(Collections.emptyList());

        DonorHealthDTO result = donorService.GetDonorHealth(3L);

        assertNotNull(result);
        double expectedBmi = 85.0 / (1.90 * 1.90);
        assertEquals(expectedBmi, result.getBmi(), 0.01);
    }

    @Test
    void getDonorHealth_shouldThrowNotFound_whenDonorNotExist() {
        when(donorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> donorService.GetDonorHealth(99L));
    }

    @Test
    void getDonorHealth_shouldThrowNotFound_whenInactive() {
        DonorEntity inactive = DonorEntity.builder()
                .id(2L).firstName("Inactive").lastName("User")
                .birthdate(LocalDate.of(1985, 1, 1)).document("99999999")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.O)
                .gender(Gender.FEMALE).height(1.60).weight(55.0)
                .email("inactive@test.com").phoneNumber("0000000000")
                .isActive(false).build();

        when(donorRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> donorService.GetDonorHealth(2L));
    }

    // ── GetBloodTypePercentage: null values ──

    @Test
    void getBloodTypePercentage_shouldHandleNullBloodGroupAndFactor() {
        Object[] rowNullGroup = new Object[]{null, BloodFactor.POSITIVE, 3L};
        Object[] rowNullFactor = new Object[]{BloodGroup.O, null, 2L};
        Object[] rowBothNull = new Object[]{null, null, 1L};

        when(donorRepository.countActiveDonors()).thenReturn(10L);
        when(donorRepository.countBloodTypesGlobally())
                .thenReturn(Arrays.asList(rowNullGroup, rowNullFactor, rowBothNull));

        List<BloodTypePercentageDTO> result = donorService.GetBloodTypePercentage();

        assertEquals(3, result.size());
        assertEquals("Sin grupo_POSITIVE", result.get(0).getBloodType());
        assertEquals("O_Sin factor", result.get(1).getBloodType());
        assertEquals("Sin grupo_Sin factor", result.get(2).getBloodType());
    }

    @Test
    void getBloodTypePercentage_shouldReturnZero_whenTotalDonorsZero() {
        Object[] row = new Object[]{BloodGroup.A, BloodFactor.POSITIVE, 5L};

        when(donorRepository.countActiveDonors()).thenReturn(0L);
        when(donorRepository.countBloodTypesGlobally())
                .thenReturn(Collections.singletonList(row));

        List<BloodTypePercentageDTO> result = donorService.GetBloodTypePercentage();

        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getPercentage(), 0.01);
    }

    // ── GetAllDonors: catch block and EntityToDTO failure ──

    @Test
    void getAllDonors_shouldThrowResponseStatus_whenRepositoryFails() {
        when(donorRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(ResponseStatusException.class,
                () -> donorService.GetAllDonors());
    }

    @Test
    void getAllDonors_shouldSkipBrokenEntities() {
        DonorEntity brokenEntity = mock(DonorEntity.class);
        when(brokenEntity.getIsActive()).thenReturn(true);
        when(brokenEntity.getId()).thenThrow(new RuntimeException("corrupt data"));

        when(donorRepository.findAll()).thenReturn(Arrays.asList(entity, brokenEntity));

        assertThrows(ResponseStatusException.class,
                () -> donorService.GetAllDonors());
    }

    // ── UpdateDonor: catch block and save failure ──

    @Test
    void updateDonor_shouldThrowResponseStatus_whenSaveFails() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(donorRepository.save(any(DonorEntity.class)))
                .thenThrow(new RuntimeException("DB write error"));

        RequestDonorDTO updateDto = RequestDonorDTO.builder()
                .firstName("Carlos").lastName("García")
                .birthdate(LocalDate.of(1988, 3, 20)).document("87654321")
                .bloodFactor(BloodFactor.NEGATIVE).bloodGroup(BloodGroup.B)
                .gender(Gender.MALE).height(1.80).weight(80.0)
                .email("carlos@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.UpdateDonor(updateDto, 1L));
    }

    @Test
    void updateDonor_shouldThrowResponseStatus_whenEntityToDTOFails() {
        DonorEntity mockEntity = mock(DonorEntity.class);
        when(mockEntity.getIsActive()).thenReturn(true);
        when(donorRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        DonorEntity brokenSaved = mock(DonorEntity.class);
        when(brokenSaved.getId()).thenThrow(new RuntimeException("mapping error"));
        when(donorRepository.save(any(DonorEntity.class))).thenReturn(brokenSaved);

        RequestDonorDTO updateDto = RequestDonorDTO.builder()
                .firstName("Test").lastName("User")
                .birthdate(LocalDate.of(1990, 1, 1)).document("12345678")
                .bloodFactor(BloodFactor.POSITIVE).bloodGroup(BloodGroup.A)
                .gender(Gender.MALE).height(1.75).weight(70.0)
                .email("test@test.com").phoneNumber("1234567890")
                .build();

        assertThrows(ResponseStatusException.class,
                () -> donorService.UpdateDonor(updateDto, 1L));
    }

    // ── CreateDonor: catch block ──

    @Test
    void createDonor_shouldThrowResponseStatus_whenRepositorySaveFails() {
        when(donorRepository.save(any(DonorEntity.class)))
                .thenThrow(new RuntimeException("DB constraint violation"));

        assertThrows(ResponseStatusException.class,
                () -> donorService.CreateDonor(request));
    }

    // ── DeleteDonor: catch block ──

    @Test
    void deleteDonor_shouldThrowResponseStatus_whenRepositorySaveFails() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(donorRepository.save(any(DonorEntity.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(ResponseStatusException.class,
                () -> donorService.DeleteDonor(1L));
    }
}
