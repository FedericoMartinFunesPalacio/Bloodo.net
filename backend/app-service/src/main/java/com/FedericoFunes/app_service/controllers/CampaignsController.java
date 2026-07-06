package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.campaigns.BloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.campaigns.GeographicDistributionDTO;
import com.FedericoFunes.app_service.dtos.campaigns.LivesSavedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.RequestCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.SubscribedDonorDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalBloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalLivesSavedDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
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

    @Operation(summary = "Estimación de sangre donada por campaña", description = "Devuelve la cantidad estimada de sangre donada por cada campaña activa y finalizada, calculada como suscriptores × 450 ml.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/metrics/blood-estimated")
    public ResponseEntity<List<BloodEstimatedDTO>> getBloodEstimatedPerCampaign() {
        return ResponseEntity.ok(campaignsService.getBloodEstimatedPerCampaign());
    }

    @Operation(summary = "Estimación total de sangre donada", description = "Devuelve el total estimado de sangre recolectada en todas las campañas del sistema.")
    @ApiResponse(responseCode = "200", description = "Métrica total obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/metrics/blood-total")
    public ResponseEntity<TotalBloodEstimatedDTO> getTotalBloodEstimated() {
        return ResponseEntity.ok(campaignsService.getTotalBloodEstimated());
    }

    @Operation(summary = "Ranking de tipos de sangre en una campaña", description = "Devuelve el conteo de tipos de sangre de los donadores suscritos. Si la campaña tiene bloodGroupRequired, devuelve lista vacía.")
    @ApiResponse(responseCode = "200", description = "Ranking obtenido correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/metrics/blood-type-ranking/{campaignId}")
    public ResponseEntity<List<BloodTypeRankingDTO>> getBloodTypeRanking(@PathVariable Long campaignId) {
        return ResponseEntity.ok(campaignsService.getBloodTypeRanking(campaignId));
    }

    @Operation(summary = "Estimación de vidas salvadas por campaña", description = "Devuelve la cantidad estimada de vidas salvadas por cada campaña (suscriptores × 3).")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/metrics/lives-saved")
    public ResponseEntity<List<LivesSavedDTO>> getLivesSavedPerCampaign() {
        return ResponseEntity.ok(campaignsService.getLivesSavedPerCampaign());
    }

    @Operation(summary = "Estimación total de vidas salvadas", description = "Devuelve el total de vidas estimadas salvadas en campañas finalizadas.")
    @ApiResponse(responseCode = "200", description = "Métrica total obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/metrics/lives-saved-total")
    public ResponseEntity<TotalLivesSavedDTO> getTotalLivesSaved() {
        return ResponseEntity.ok(campaignsService.getTotalLivesSaved());
    }

    @Operation(summary = "Obtener todas las campañas", description = "Devuelve una lista con todas las campañas activas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/")
    public ResponseEntity<List<ResponseCampaignsDTO>> getAllCampaigns() {
        return ResponseEntity.ok(campaignsService.getAllCampaigns());
    }

    @Operation(summary = "Obtener campañas de un organizador", description = "Devuelve las campañas activas y sin finalizar creadas por un organizador específico.")
    @ApiResponse(responseCode = "200", description = "Lista de campañas del organizador obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<ResponseCampaignsDTO>> getCampaignsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getCampaignsByOrganizer(organizerId));
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

    @Operation(summary = "Sangre total recolectada por organizador", description = "Devuelve el total estimado de sangre recolectada en todas las campañas de un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/blood-total")
    public ResponseEntity<TotalBloodEstimatedDTO> getTotalBloodByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getTotalBloodByOrganizer(organizerId));
    }

    @Operation(summary = "Porcentaje de tipos de sangre por organizador", description = "Devuelve la distribución porcentual de tipos de sangre de los donadores en las campañas de un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/blood-type-percentage")
    public ResponseEntity<List<BloodTypePercentageDTO>> getBloodTypePercentageByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getBloodTypePercentageByOrganizer(organizerId));
    }

    @Operation(summary = "Cantidad de campañas creadas por organizador", description = "Devuelve el total de campañas creadas por un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/campaign-count")
    public ResponseEntity<Long> getCampaignCountByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getCampaignCountByOrganizer(organizerId));
    }

    @Operation(summary = "Cantidad de campañas finalizadas por organizador", description = "Devuelve el total de campañas finalizadas de un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/finished-count")
    public ResponseEntity<Long> getFinishedCampaignCountByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getFinishedCampaignCountByOrganizer(organizerId));
    }

    @Operation(summary = "Total de donadores en campañas del organizador", description = "Devuelve la cantidad total de suscripciones de donadores en todas las campañas de un organizador (incluye repetidos).")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/total-donors")
    public ResponseEntity<Long> getTotalDonorsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getTotalDonorsByOrganizer(organizerId));
    }

    @Operation(summary = "Promedio de donadores por campaña", description = "Devuelve el promedio de donadores suscritos por campaña de un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/average-donors")
    public ResponseEntity<Double> getAverageDonorsPerCampaign(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getAverageDonorsPerCampaign(organizerId));
    }

    @Operation(summary = "Vidas salvadas por organizador", description = "Devuelve la estimación de vidas salvadas por un organizador (total suscriptores × 3).")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/lives-saved")
    public ResponseEntity<TotalLivesSavedDTO> getLivesSavedByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getLivesSavedByOrganizer(organizerId));
    }

    @Operation(summary = "Distribución geográfica de campañas", description = "Devuelve las ubicaciones (latitud, longitud) de todas las campañas de un organizador.")
    @ApiResponse(responseCode = "200", description = "Métrica obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/metrics/organizer/{organizerId}/geographic-distribution")
    public ResponseEntity<List<GeographicDistributionDTO>> getGeographicDistributionByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(campaignsService.getGeographicDistributionByOrganizer(organizerId));
    }

    @Operation(summary = "Campañas activas suscritas por donador", description = "Devuelve la lista de campañas activas (sin finalizar) a las que un donador está suscrito.")
    @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/subscribed-by/{donorId}")
    public ResponseEntity<List<ResponseCampaignsDTO>> getActiveSubscribedCampaigns(@PathVariable Long donorId) {
        return ResponseEntity.ok(campaignsService.getActiveSubscribedCampaigns(donorId));
    }
}
