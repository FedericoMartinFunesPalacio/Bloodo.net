package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.organizerper.RequestOrganizerPerDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.entities.OrganizerPerEntity;
import com.FedericoFunes.app_service.entities.enums.Gender;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.OrganizerPerRepository;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import com.FedericoFunes.app_service.services.impl.OrganizerPerServiceImpl;
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
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizerPerServiceImplTest {

    @Mock
    private OrganizerPerRepository organizerPerRepository;

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private OrganizerPerServiceImpl organizerPerService;

    private RequestOrganizerPerDTO request;
    private OrganizerPerEntity entity;

    private OrganizerPerEntity buildEntity(Long id, String firstName, String lastName, boolean isActive) {
        OrganizerPerEntity e = OrganizerPerEntity.builder()
                .firstName(firstName).lastName(lastName)
                .birthdate(LocalDate.of(1985, 8, 20))
                .gender(Gender.FEMALE).build();
        e.setId(id);
        e.setDocument("87654321");
        e.setDirection("Av. Corrientes 1234, Buenos Aires");
        e.setLatitude(-34.6037);
        e.setLongitude(-58.3816);
        e.setEmail("maria@test.com");
        e.setPhoneNumber("0987654321");
        e.setIsActive(isActive);
        return e;
    }

    @BeforeEach
    void setUp() throws Exception {
        request = RequestOrganizerPerDTO.builder()
                .firstName("María")
                .lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20))
                .document("87654321")
                .direction("Av. Corrientes 1234, Buenos Aires")
                .gender(Gender.FEMALE)
                .email("maria@test.com")
                .phoneNumber("0987654321")
                .build();

        entity = buildEntity(1L, "María", "López", true);

        lenient().when(googleMapsService.getLatLngFromAddress(anyString()))
                .thenReturn(new double[]{-34.6037, -58.3816});
    }

    // ── GetAllOrganizerPers ──

    @Test
    void getAllOrganizerPers_shouldReturnOnlyActive() {
        OrganizerPerEntity inactive = buildEntity(2L, "Inactive", "User", false);

        when(organizerPerRepository.findAll()).thenReturn(Arrays.asList(entity, inactive));

        var result = organizerPerService.GetAllOrganizerPers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("María", result.get(0).getFirstName());
    }

    @Test
    void getAllOrganizerPers_shouldReturnEmpty_whenNoneActive() {
        OrganizerPerEntity inactive = buildEntity(2L, "Inactive", "User", false);

        when(organizerPerRepository.findAll()).thenReturn(Collections.singletonList(inactive));

        var result = organizerPerService.GetAllOrganizerPers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllOrganizerPers_shouldReturnEmpty_whenRepositoryEmpty() {
        when(organizerPerRepository.findAll()).thenReturn(Collections.emptyList());

        var result = organizerPerService.GetAllOrganizerPers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllOrganizerPers_shouldSkipEntitiesWithNullIsActive() {
        OrganizerPerEntity nullActive = buildEntity(3L, "NullActive", "User", true);
        nullActive.setIsActive(null);

        when(organizerPerRepository.findAll()).thenReturn(Collections.singletonList(nullActive));

        var result = organizerPerService.GetAllOrganizerPers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── GetOrganizerPerById ──

    @Test
    void getOrganizerPerById_shouldReturnDto_whenFoundAndActive() {
        when(organizerPerRepository.findById(1L)).thenReturn(Optional.of(entity));

        ResponseOrganizerPerDTO result = organizerPerService.GetOrganizerPerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("María", result.getFirstName());
        assertEquals("López", result.getLastName());
        assertEquals("maria@test.com", result.getEmail());
        assertEquals(-34.6037, result.getLatitude(), 0.001);
        assertTrue(result.getIsActive());
    }

    @Test
    void getOrganizerPerById_shouldThrowNotFound_whenNotExist() {
        when(organizerPerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerPerService.GetOrganizerPerById(99L));
    }

    @Test
    void getOrganizerPerById_shouldThrowNotFound_whenInactive() {
        OrganizerPerEntity inactive = buildEntity(2L, "Inactive", "User", false);

        when(organizerPerRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> organizerPerService.GetOrganizerPerById(2L));
    }

    @Test
    void getOrganizerPerById_shouldThrowNotFound_whenIsActiveNull() {
        OrganizerPerEntity nullActive = buildEntity(3L, "NullActive", "User", true);
        nullActive.setIsActive(null);

        when(organizerPerRepository.findById(3L)).thenReturn(Optional.of(nullActive));

        assertThrows(NotFoundException.class,
                () -> organizerPerService.GetOrganizerPerById(3L));
    }

    // ── CreateOrganizerPer ──

    @Test
    void createOrganizerPer_shouldReturnCreatedDto() throws Exception {
        when(organizerPerRepository.save(any(OrganizerPerEntity.class))).thenReturn(entity);

        ResponseOrganizerPerDTO result = organizerPerService.CreateOrganizerPer(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("María", result.getFirstName());
        assertEquals("López", result.getLastName());
        assertEquals("maria@test.com", result.getEmail());
        assertTrue(result.getIsActive());
        verify(organizerPerRepository, times(1)).save(any(OrganizerPerEntity.class));
        verify(googleMapsService, times(1)).getLatLngFromAddress("Av. Corrientes 1234, Buenos Aires");
    }

    @Test
    void createOrganizerPer_shouldSetCoordinatesFromGoogleMaps() throws Exception {
        when(googleMapsService.getLatLngFromAddress("Av. Corrientes 1234, Buenos Aires"))
                .thenReturn(new double[]{-34.6037, -58.3816});
        when(organizerPerRepository.save(any(OrganizerPerEntity.class))).thenReturn(entity);

        organizerPerService.CreateOrganizerPer(request);

        verify(organizerPerRepository).save(argThat(e ->
                e.getLatitude() == -34.6037 && e.getLongitude() == -58.3816
        ));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenNull() {
        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(null));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenFirstNameBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenEmailBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    // ── validateOrganizerPer: individual field null tests ──

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenFirstNameNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName(null).lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenLastNameNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName(null)
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenLastNameBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenBirthdateNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(null).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenDocumentNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document(null)
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenDocumentBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenDirectionNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction(null).gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenDirectionBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("  ").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenGenderNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(null)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenEmailNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email(null).phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenPhoneNumberNull() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber(null)
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    @Test
    void createOrganizerPer_shouldThrowBadRequest_whenPhoneNumberBlank() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("  ")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    // ── DTOToEntity: catch blocks ──

    @Test
    void createOrganizerPer_shouldThrowResponseStatus_whenGoogleMapsFails() throws Exception {
        when(googleMapsService.getLatLngFromAddress(anyString()))
                .thenThrow(new RuntimeException("API quota exceeded"));

        assertThrows(ResponseStatusException.class,
                () -> organizerPerService.CreateOrganizerPer(request));
    }

    @Test
    void createOrganizerPer_shouldThrowResponseStatus_whenEntityMappingFails() throws Exception {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName("María").lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        when(googleMapsService.getLatLngFromAddress(anyString()))
                .thenReturn(new double[]{-34.6037, -58.3816});
        when(organizerPerRepository.save(any(OrganizerPerEntity.class)))
                .thenThrow(new RuntimeException("DB constraint error"));

        assertThrows(RuntimeException.class,
                () -> organizerPerService.CreateOrganizerPer(invalid));
    }

    // ── UpdateOrganizerPer ──

    @Test
    void updateOrganizerPer_shouldReturnUpdatedDto() throws Exception {
        when(organizerPerRepository.findById(1L)).thenReturn(Optional.of(entity));
        OrganizerPerEntity updated = buildEntity(1L, "Ana", "García", true);
        when(organizerPerRepository.save(any(OrganizerPerEntity.class))).thenReturn(updated);

        RequestOrganizerPerDTO updateDto = RequestOrganizerPerDTO.builder()
                .firstName("Ana").lastName("García")
                .birthdate(LocalDate.of(1990, 3, 10)).document("11223344")
                .direction("Av. Libertador 5678").gender(Gender.FEMALE)
                .email("ana@test.com").phoneNumber("5566778899")
                .build();

        ResponseOrganizerPerDTO result = organizerPerService.UpdateOrganizerPer(updateDto, 1L);

        assertNotNull(result);
        assertEquals("Ana", result.getFirstName());
        assertEquals("García", result.getLastName());
        verify(organizerPerRepository, times(1)).findById(1L);
        verify(organizerPerRepository, times(1)).save(any(OrganizerPerEntity.class));
    }

    @Test
    void updateOrganizerPer_shouldThrowNotFound_whenNotExist() {
        when(organizerPerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerPerService.UpdateOrganizerPer(request, 99L));
    }

    @Test
    void updateOrganizerPer_shouldThrowNotFound_whenInactive() {
        OrganizerPerEntity inactive = buildEntity(2L, "Inactive", "User", false);

        when(organizerPerRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> organizerPerService.UpdateOrganizerPer(request, 2L));
    }

    @Test
    void updateOrganizerPer_shouldThrowBadRequest_whenValidationFails() {
        RequestOrganizerPerDTO invalid = RequestOrganizerPerDTO.builder()
                .firstName(null).lastName("López")
                .birthdate(LocalDate.of(1985, 8, 20)).document("87654321")
                .direction("Av. Corrientes 1234").gender(Gender.FEMALE)
                .email("maria@test.com").phoneNumber("0987654321")
                .build();

        assertThrows(BadRequestException.class,
                () -> organizerPerService.UpdateOrganizerPer(invalid, 1L));
    }

    // ── DeleteOrganizerPer ──

    @Test
    void deleteOrganizerPer_shouldSoftDelete() {
        when(organizerPerRepository.findById(1L)).thenReturn(Optional.of(entity));
        OrganizerPerEntity deleted = buildEntity(1L, "María", "López", false);
        when(organizerPerRepository.save(any(OrganizerPerEntity.class))).thenReturn(deleted);

        ResponseOrganizerPerDTO result = organizerPerService.DeleteOrganizerPer(1L);

        assertNotNull(result);
        assertFalse(result.getIsActive());
        verify(organizerPerRepository, times(1)).save(any(OrganizerPerEntity.class));
    }

    @Test
    void deleteOrganizerPer_shouldThrowNotFound_whenNotExist() {
        when(organizerPerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerPerService.DeleteOrganizerPer(99L));
    }
}
