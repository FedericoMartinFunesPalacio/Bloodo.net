package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.organizeremp.RequestOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.entities.OrganizerEmpEntity;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.OrganizerEmpRepository;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import com.FedericoFunes.app_service.services.impl.OrganizerEmpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizerEmpServiceImplTest {

    @Mock
    private OrganizerEmpRepository organizerEmpRepository;

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private OrganizerEmpServiceImpl organizerEmpService;

    private RequestOrganizerEmpDTO request;
    private OrganizerEmpEntity entity;

    private OrganizerEmpEntity buildEntity(Long id, String fullName, boolean isActive) {
        OrganizerEmpEntity e = OrganizerEmpEntity.builder().fullName(fullName).build();
        e.setId(id);
        e.setDocument("30123456");
        e.setDirection("Av. Libertador 5678, Buenos Aires");
        e.setLatitude(-34.5000);
        e.setLongitude(-58.4000);
        e.setEmail("acme@corp.com");
        e.setPhoneNumber("1122334455");
        e.setIsActive(isActive);
        return e;
    }

    @BeforeEach
    void setUp() throws Exception {
        request = RequestOrganizerEmpDTO.builder()
                .fullName("Acme S.A.")
                .document("30123456")
                .direction("Av. Libertador 5678, Buenos Aires")
                .email("acme@corp.com")
                .phoneNumber("1122334455")
                .build();

        entity = buildEntity(1L, "Acme S.A.", true);

        lenient().when(googleMapsService.getLatLngFromAddress(anyString()))
                .thenReturn(new double[]{-34.5000, -58.4000});
    }

    // ── GetAllOrganizerEmps ──

    @Test
    void getAllOrganizerEmps_shouldReturnOnlyActive() {
        OrganizerEmpEntity inactive = buildEntity(2L, "Closed Corp", false);

        when(organizerEmpRepository.findAll()).thenReturn(Arrays.asList(entity, inactive));

        var result = organizerEmpService.GetAllOrganizerEmps();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Acme S.A.", result.get(0).getFullName());
    }

    @Test
    void getAllOrganizerEmps_shouldReturnEmpty_whenNoneActive() {
        OrganizerEmpEntity inactive = buildEntity(2L, "Closed Corp", false);

        when(organizerEmpRepository.findAll()).thenReturn(Collections.singletonList(inactive));

        var result = organizerEmpService.GetAllOrganizerEmps();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllOrganizerEmps_shouldReturnEmpty_whenRepositoryEmpty() {
        when(organizerEmpRepository.findAll()).thenReturn(Collections.emptyList());

        var result = organizerEmpService.GetAllOrganizerEmps();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllOrganizerEmps_shouldSkipEntitiesWithNullIsActive() {
        OrganizerEmpEntity nullActive = buildEntity(3L, "Null Active", true);
        nullActive.setIsActive(null);

        when(organizerEmpRepository.findAll()).thenReturn(Collections.singletonList(nullActive));

        var result = organizerEmpService.GetAllOrganizerEmps();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── GetOrganizerEmpById ──

    @Test
    void getOrganizerEmpById_shouldReturnDto_whenFoundAndActive() {
        when(organizerEmpRepository.findById(1L)).thenReturn(Optional.of(entity));

        ResponseOrganizerEmpDTO result = organizerEmpService.GetOrganizerEmpById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Acme S.A.", result.getFullName());
        assertEquals("acme@corp.com", result.getEmail());
        assertEquals(-34.5000, result.getLatitude(), 0.001);
        assertTrue(result.getIsActive());
    }

    @Test
    void getOrganizerEmpById_shouldThrowNotFound_whenNotExist() {
        when(organizerEmpRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.GetOrganizerEmpById(99L));
    }

    @Test
    void getOrganizerEmpById_shouldThrowNotFound_whenInactive() {
        OrganizerEmpEntity inactive = buildEntity(2L, "Closed Corp", false);

        when(organizerEmpRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.GetOrganizerEmpById(2L));
    }

    @Test
    void getOrganizerEmpById_shouldThrowNotFound_whenIsActiveNull() {
        OrganizerEmpEntity nullActive = buildEntity(3L, "Null Active", true);
        nullActive.setIsActive(null);

        when(organizerEmpRepository.findById(3L)).thenReturn(Optional.of(nullActive));

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.GetOrganizerEmpById(3L));
    }

    // ── CreateOrganizerEmp ──

    @Test
    void createOrganizerEmp_shouldReturnCreatedDto() throws Exception {
        when(organizerEmpRepository.save(any(OrganizerEmpEntity.class))).thenReturn(entity);

        ResponseOrganizerEmpDTO result = organizerEmpService.CreateOrganizerEmp(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Acme S.A.", result.getFullName());
        assertEquals("acme@corp.com", result.getEmail());
        assertTrue(result.getIsActive());
        verify(organizerEmpRepository, times(1)).save(any(OrganizerEmpEntity.class));
        verify(googleMapsService, times(1)).getLatLngFromAddress("Av. Libertador 5678, Buenos Aires");
    }

    @Test
    void createOrganizerEmp_shouldSetCoordinatesFromGoogleMaps() throws Exception {
        when(googleMapsService.getLatLngFromAddress("Av. Libertador 5678, Buenos Aires"))
                .thenReturn(new double[]{-34.5000, -58.4000});
        when(organizerEmpRepository.save(any(OrganizerEmpEntity.class))).thenReturn(entity);

        organizerEmpService.CreateOrganizerEmp(request);

        verify(organizerEmpRepository).save(argThat(e ->
                e.getLatitude() == -34.5000 && e.getLongitude() == -58.4000
        ));
    }

    @Test
    void createOrganizerEmp_shouldThrowBadRequest_whenNull() {
        assertThrows(BadRequestException.class,
                () -> organizerEmpService.CreateOrganizerEmp(null));
    }

    @Test
    void createOrganizerEmp_shouldThrowBadRequest_whenFullNameBlank() {
        RequestOrganizerEmpDTO invalid = RequestOrganizerEmpDTO.builder()
                .fullName("").document("30123456")
                .direction("Av. Libertador 5678").email("acme@corp.com")
                .phoneNumber("1122334455").build();

        assertThrows(BadRequestException.class,
                () -> organizerEmpService.CreateOrganizerEmp(invalid));
    }

    @Test
    void createOrganizerEmp_shouldThrowBadRequest_whenEmailBlank() {
        RequestOrganizerEmpDTO invalid = RequestOrganizerEmpDTO.builder()
                .fullName("Acme S.A.").document("30123456")
                .direction("Av. Libertador 5678").email("")
                .phoneNumber("1122334455").build();

        assertThrows(BadRequestException.class,
                () -> organizerEmpService.CreateOrganizerEmp(invalid));
    }

    @Test
    void createOrganizerEmp_shouldThrowBadRequest_whenPhoneNumberBlank() {
        RequestOrganizerEmpDTO invalid = RequestOrganizerEmpDTO.builder()
                .fullName("Acme S.A.").document("30123456")
                .direction("Av. Libertador 5678").email("acme@corp.com")
                .phoneNumber("").build();

        assertThrows(BadRequestException.class,
                () -> organizerEmpService.CreateOrganizerEmp(invalid));
    }

    // ── UpdateOrganizerEmp ──

    @Test
    void updateOrganizerEmp_shouldReturnUpdatedDto() throws Exception {
        when(organizerEmpRepository.findById(1L)).thenReturn(Optional.of(entity));
        OrganizerEmpEntity updated = buildEntity(1L, "Acme Global", true);
        when(organizerEmpRepository.save(any(OrganizerEmpEntity.class))).thenReturn(updated);

        RequestOrganizerEmpDTO updateDto = RequestOrganizerEmpDTO.builder()
                .fullName("Acme Global").document("30123456")
                .direction("Av. Libertador 9999").email("global@corp.com")
                .phoneNumber("9988776655").build();

        ResponseOrganizerEmpDTO result = organizerEmpService.UpdateOrganizerEmp(updateDto, 1L);

        assertNotNull(result);
        assertEquals("Acme Global", result.getFullName());
        verify(organizerEmpRepository, times(1)).findById(1L);
        verify(organizerEmpRepository, times(1)).save(any(OrganizerEmpEntity.class));
    }

    @Test
    void updateOrganizerEmp_shouldThrowNotFound_whenNotExist() {
        when(organizerEmpRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.UpdateOrganizerEmp(request, 99L));
    }

    @Test
    void updateOrganizerEmp_shouldThrowNotFound_whenInactive() {
        OrganizerEmpEntity inactive = buildEntity(2L, "Closed Corp", false);

        when(organizerEmpRepository.findById(2L)).thenReturn(Optional.of(inactive));

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.UpdateOrganizerEmp(request, 2L));
    }

    @Test
    void updateOrganizerEmp_shouldThrowBadRequest_whenValidationFails() {
        RequestOrganizerEmpDTO invalid = RequestOrganizerEmpDTO.builder()
                .fullName(null).document("30123456")
                .direction("Av. Libertador 5678").email("acme@corp.com")
                .phoneNumber("1122334455").build();

        assertThrows(BadRequestException.class,
                () -> organizerEmpService.UpdateOrganizerEmp(invalid, 1L));
    }

    // ── DeleteOrganizerEmp ──

    @Test
    void deleteOrganizerEmp_shouldSoftDelete() {
        when(organizerEmpRepository.findById(1L)).thenReturn(Optional.of(entity));
        OrganizerEmpEntity deleted = buildEntity(1L, "Acme S.A.", false);
        when(organizerEmpRepository.save(any(OrganizerEmpEntity.class))).thenReturn(deleted);

        ResponseOrganizerEmpDTO result = organizerEmpService.DeleteOrganizerEmp(1L);

        assertNotNull(result);
        assertFalse(result.getIsActive());
        verify(organizerEmpRepository, times(1)).save(any(OrganizerEmpEntity.class));
    }

    @Test
    void deleteOrganizerEmp_shouldThrowNotFound_whenNotExist() {
        when(organizerEmpRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> organizerEmpService.DeleteOrganizerEmp(99L));
    }
}
