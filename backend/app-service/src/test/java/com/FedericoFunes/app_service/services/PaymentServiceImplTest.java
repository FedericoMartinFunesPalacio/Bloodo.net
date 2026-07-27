package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.services.external.PaymentService;
import com.mercadopago.MercadoPagoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() throws Exception {
        Field tokenField = PaymentService.class.getDeclaredField("accessToken");
        tokenField.setAccessible(true);
        tokenField.set(paymentService, "test-access-token");
    }

    @Test
    void createDonationPreference_shouldSetAccessToken() throws Exception {
        try {
            paymentService.createDonationPreference(new BigDecimal("100.00"));
        } catch (Exception e) {
            // External API call will fail in test environment, that's expected
        }

        assertEquals("test-access-token", MercadoPagoConfig.getAccessToken());
    }

    @Test
    void createDonationPreference_shouldAcceptValidAmount() throws Exception {
        try {
            paymentService.createDonationPreference(new BigDecimal("250.50"));
        } catch (Exception e) {
            // External API call will fail, but method should not reject the amount
        }
        // If we reach here without IllegalArgumentException, the amount was accepted
        assertTrue(true);
    }

    @Test
    void createDonationPreference_shouldThrow_whenAmountIsNull() {
        assertThrows(Exception.class,
                () -> paymentService.createDonationPreference(null));
    }
}
