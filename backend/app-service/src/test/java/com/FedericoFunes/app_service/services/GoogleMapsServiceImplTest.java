package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleMapsServiceImplTest {

    @InjectMocks
    private GoogleMapsService googleMapsService;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        Field rtField = GoogleMapsService.class.getDeclaredField("restTemplate");
        rtField.setAccessible(true);
        rtField.set(googleMapsService, restTemplate);

        Field apiKeyField = GoogleMapsService.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(googleMapsService, "test-api-key");
    }

    @Test
    void getLatLngFromAddress_shouldReturnCoordinates() throws Exception {
        String json = "{\"results\":[{\"geometry\":{\"location\":{\"lat\":-34.6037,\"lng\":-58.3816}}}]}";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        double[] result = googleMapsService.getLatLngFromAddress("Av. Corrientes 1234, Buenos Aires");

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(-34.6037, result[0], 0.001);
        assertEquals(-58.3816, result[1], 0.001);
    }

    @Test
    void getLatLngFromAddress_shouldReplaceSpacesWithPlus() throws Exception {
        String json = "{\"results\":[{\"geometry\":{\"location\":{\"lat\":-34.0,\"lng\":-58.0}}}]}";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        googleMapsService.getLatLngFromAddress("Calle Falsa 123");

        verify(restTemplate).getForEntity(
                contains("Calle+Falsa+123"),
                eq(String.class)
        );
    }

    @Test
    void getLatLngFromAddress_shouldIncludeApiKey() throws Exception {
        String json = "{\"results\":[{\"geometry\":{\"location\":{\"lat\":0.0,\"lng\":0.0}}}]}";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        googleMapsService.getLatLngFromAddress("Test");

        verify(restTemplate).getForEntity(
                contains("key=test-api-key"),
                eq(String.class)
        );
    }

    @Test
    void getLatLngFromAddress_shouldThrow_whenRestTemplateFails() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        assertThrows(RuntimeException.class,
                () -> googleMapsService.getLatLngFromAddress("Test"));
    }

    @Test
    void getLatLngFromAddress_shouldThrow_whenResponseInvalidJson() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("not json"));

        assertThrows(Exception.class,
                () -> googleMapsService.getLatLngFromAddress("Test"));
    }

    @Test
    void getLatLngFromAddress_shouldThrow_whenNoResults() {
        String json = "{\"results\":[]}";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        assertThrows(Exception.class,
                () -> googleMapsService.getLatLngFromAddress("Invalid address"));
    }
}
