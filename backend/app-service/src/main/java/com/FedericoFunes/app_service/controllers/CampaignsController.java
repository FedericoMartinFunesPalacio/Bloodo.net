package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.campaigns.RequestCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.SubscribedDonorDTO;
import com.FedericoFunes.app_service.services.CampaignsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/campaigns")
public class CampaignsController {
    private final CampaignsService campaignsService;

    @Operation(summary = "Obtener todas las campañas", description = "Devuelve una lista con todas las campañas activas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/")
    public ResponseEntity<List<ResponseCampaignsDTO>> getAllCampaigns() {
        return ResponseEntity.ok(campaignsService.getAllCampaigns());
    }

    @Operation(summary = "Obtener una campaña por ID", description = "Devuelve la información detallada de una campaña específica identificada por su ID.")
    @ApiResponse(responseCode = "200", description = "Campaña encontrada y devuelta correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCampaignsDTO> getCampaignById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignsService.getCampaignById(id));
    }

    @Operation(summary = "Crear una nueva campaña", description = "Registra una nueva campaña en el sistema a partir de los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Campaña creada exitosamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping("/")
    public ResponseEntity<ResponseCampaignsDTO> createCampaign(@RequestBody RequestCampaignsDTO campaign) {
        return ResponseEntity.ok(campaignsService.createCampaign(campaign));
    }

    @Operation(summary = "Actualizar una campaña existente", description = "Actualiza la información de una campaña identificada por su ID con los nuevos datos proporcionados. Solo campañas activas pueden ser actualizadas.")
    @ApiResponse(responseCode = "200", description = "Campaña actualizada correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseCampaignsDTO> updateCampaign(@RequestBody RequestCampaignsDTO campaign, @PathVariable Long id) {
        return ResponseEntity.ok(campaignsService.updateCampaign(campaign, id));
    }

    @Operation(summary = "Eliminar (desactivar) una campaña", description = "Desactiva una campaña identificada por su ID, marcándola como inactiva en el sistema.")
    @ApiResponse(responseCode = "200", description = "Campaña desactivada correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseCampaignsDTO> deleteCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignsService.deleteCampaign(id));
    }

    @Operation(summary = "Suscribir donante a campaña", description = "Agrega un donante a la lista de donantes suscriptos a una campaña activa y no terminada.")
    @ApiResponse(responseCode = "200", description = "Donante suscripto correctamente a la campaña.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @PostMapping("/{campaignId}/subscribe/{donorId}")
    public ResponseEntity<ResponseCampaignsDTO> subscribeDonor(@PathVariable Long campaignId, @PathVariable Long donorId) {
        return ResponseEntity.ok(campaignsService.subscribeDonor(campaignId, donorId));
    }

    @Operation(summary = "Desuscribir donante de campaña", description = "Elimina un donante de la lista de suscriptos a una campaña activa y no terminada. Devuelve la lista actualizada de donantes suscriptos.")
    @ApiResponse(responseCode = "200", description = "Lista de donantes suscriptos actualizada tras la desuscripción.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @DeleteMapping("/{campaignId}/unsubscribe/{donorId}")
    public ResponseEntity<List<SubscribedDonorDTO>> unsubscribeDonor(@PathVariable Long campaignId, @PathVariable Long donorId) {
        return ResponseEntity.ok(campaignsService.unsubscribeDonor(campaignId, donorId));
    }

    @Operation(summary = "Finalizar campaña", description = "Define la fecha de finalización de una campaña. Una vez finalizada, no puede ser modificada ni eliminada.")
    @ApiResponse(responseCode = "200", description = "Campaña finalizada correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{campaignId}/finish")
    public ResponseEntity<ResponseCampaignsDTO> finishCampaign(@PathVariable Long campaignId, @RequestParam("endDate") @Parameter(description = "Fecha de finalización (formato yyyy-MM-dd)") String endDate) {
        java.time.LocalDate date = java.time.LocalDate.parse(endDate);
        return ResponseEntity.ok(campaignsService.finishCampaign(campaignId, date));
    }

    @Operation(summary = "Obtener campañas finalizadas", description = "Devuelve una lista de todas las campañas que han finalizado (tienen fecha de finalización).")
    @ApiResponse(responseCode = "200", description = "Lista de campañas finalizadas obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/finished")
    public ResponseEntity<List<ResponseCampaignsDTO>> getAllFinishedCampaigns() {
        return ResponseEntity.ok(campaignsService.getAllFinishedCampaigns());
    }

    @Operation(summary = "Obtener los donadores suscriptos a una campaña", description = "Devuelve una lista de todos los donadores que se suscribieron a una campaña.")
    @ApiResponse(responseCode = "200", description = "Lista de donadores suscriptos a una campaña")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/subscribed/{campaignId}")
    public ResponseEntity<List<SubscribedDonorDTO>> getAllDonorsByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(campaignsService.getSubscribedDonors(campaignId));
    }

    @Operation(summary = "Notificar próxima campaña", description = "Notifica por email si la campaña esta próxima.")
    @ApiResponse(responseCode = "200", description = "No devuelve nada, notifica via email")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping("/{id}/notify-upcoming")
    public void notifyUpcoming(@PathVariable Long id) {
        campaignsService.notifyUpcomingCampaign(id);
    }
}
