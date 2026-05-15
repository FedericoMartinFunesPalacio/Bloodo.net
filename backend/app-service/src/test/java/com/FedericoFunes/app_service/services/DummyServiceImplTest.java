package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import com.FedericoFunes.app_service.entities.DummyEntity;
import com.FedericoFunes.app_service.repositories.DummyRepository;
import com.FedericoFunes.app_service.services.impl.DummyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DummyServiceImplTest {

    @Mock
    private DummyRepository dummyRepository;

    @InjectMocks
    private DummyServiceImpl dummyService;

    private RequestDummyDTO request;
    private DummyEntity entity;

    @BeforeEach
    void setUp() {
        request = RequestDummyDTO.builder()
                .name("John Doe")
                .description("A sample user for testing purposes.")
                .serialNumber("SN123456789")
                .createdAt(LocalDate.of(2025, 8, 1))
                .build();

        entity = DummyEntity.builder()
                .id(10L)
                .name(request.getName())
                .description(request.getDescription())
                .serialNumber(request.getSerialNumber())
                .createdAt(request.getCreatedAt())
                .build();
    }

    @Test
    void createDummy_shouldReturnCreatedDto() {
        when(dummyRepository.save(any(DummyEntity.class))).thenReturn(entity);

        ResponseDummyDTO result = dummyService.createDummy(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getSerialNumber(), result.getSerialNumber());
        assertEquals(request.getCreatedAt(), result.getCreatedAt());

        verify(dummyRepository, times(1)).save(any(DummyEntity.class));
    }

    @Test
    void updateDummy_shouldReturnUpdatedDto() {
        when(dummyRepository.findById(10L)).thenReturn(Optional.of(entity));
        DummyEntity updatedEntity = DummyEntity.builder()
                .id(10L)
                .name("Updated Name")
                .description("Updated description")
                .serialNumber("SN999")
                .createdAt(LocalDate.now())
                .build();
        when(dummyRepository.save(any(DummyEntity.class))).thenReturn(updatedEntity);

        RequestDummyDTO updateDto = RequestDummyDTO.builder()
                .name("Updated Name")
                .description("Updated description")
                .serialNumber("SN999")
                .createdAt(updatedEntity.getCreatedAt())
                .build();

        ResponseDummyDTO result = dummyService.updateDummy(updateDto, 10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals("SN999", result.getSerialNumber());

        verify(dummyRepository, times(1)).findById(10L);
        verify(dummyRepository, times(1)).save(any(DummyEntity.class));
    }

    @Test
    void getDummyById_shouldReturnDto_whenFound() {
        when(dummyRepository.findById(10L)).thenReturn(Optional.of(entity));

        ResponseDummyDTO result = dummyService.getDummyById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getSerialNumber(), result.getSerialNumber());

        verify(dummyRepository, times(1)).findById(10L);
    }

    @Test
    void getDummyById_shouldThrow_whenNotFound() {
        when(dummyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dummyService.getDummyById(99L));

        verify(dummyRepository, times(1)).findById(99L);
    }

    @Test
    void getAllDummys_shouldReturnList() {
        DummyEntity other = DummyEntity.builder()
                .id(11L)
                .name("Jane Smith")
                .description("Another sample user")
                .serialNumber("SN987654321")
                .createdAt(LocalDate.of(2025, 8, 2))
                .build();

        when(dummyRepository.findAll()).thenReturn(Arrays.asList(entity, other));

        List<ResponseDummyDTO> results = dummyService.getAllDummys();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getName().equals("John Doe")));
        assertTrue(results.stream().anyMatch(r -> r.getName().equals("Jane Smith")));

        verify(dummyRepository, times(1)).findAll();
    }
}
