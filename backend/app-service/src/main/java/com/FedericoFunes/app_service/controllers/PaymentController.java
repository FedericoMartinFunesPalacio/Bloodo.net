package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.services.external.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @Operation(summary = "Generar una preferencia de pago para donación",
            description = "Recibe un monto de donación en el cuerpo de la solicitud y crea una preferencia de pago en Mercado Pago.")
    @ApiResponse(responseCode = "200", description = "Preferencia de pago creada correctamente. La respuesta contiene el enlace de inicio de pago (`initPoint`) generado por Mercado Pago.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @PostMapping("/donate")
    public ResponseEntity<String> donate(@RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal amount = request.get("amount");
            String initPoint = paymentService.createDonationPreference(amount);
            return ResponseEntity.ok(initPoint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating preference: " + e.getMessage());
        }
    }
}

