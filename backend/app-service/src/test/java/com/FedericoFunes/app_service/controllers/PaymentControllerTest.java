package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.services.external.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void donate_shouldReturnInitPoint() throws Exception {
        when(paymentService.createDonationPreference(any(BigDecimal.class)))
                .thenReturn("https://mercadopago.com/checkout/123");

        mockMvc.perform(post("/api/v1/payments/donate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 1000}"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://mercadopago.com/checkout/123"));
    }

    @Test
    void donate_shouldReturn500_whenServiceFails() throws Exception {
        when(paymentService.createDonationPreference(any(BigDecimal.class)))
                .thenThrow(new RuntimeException("MP API error"));

        mockMvc.perform(post("/api/v1/payments/donate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating preference: MP API error"));
    }
}
