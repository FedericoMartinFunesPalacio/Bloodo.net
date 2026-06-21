package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorHealthDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorStatsDTO;
import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.services.DonorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donors")
public class DonorController {
    private final DonorService donorService;

    @Operation(summary = "Ranking global de tipos de sangre", description = "Devuelve el conteo de donadores por tipo de sangre (grupo + factor) en toda la aplicación, ordenado de mayor a menor.")
    @ApiResponse(responseCode = "200", description = "Ranking obtenido correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR', 'ORGANIZER')")
    @GetMapping("/metrics/blood-type-ranking")
    public ResponseEntity<List<BloodTypeRankingDTO>> GetBloodTypeRanking() {
        return ResponseEntity.ok(donorService.GetBloodTypeRanking());
    }

    @Operation(summary = "Porcentaje de tipos de sangre", description = "Devuelve el porcentaje de donadores por tipo de sangre respecto al total de donadores activos.")
    @ApiResponse(responseCode = "200", description = "Porcentajes obtenidos correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/metrics/blood-type-percentage")
    public ResponseEntity<List<BloodTypePercentageDTO>> GetBloodTypePercentage() {
        return ResponseEntity.ok(donorService.GetBloodTypePercentage());
    }

    @Operation(summary = "Obtener todos los donantes",
            description = "Devuelve una lista con todos los donantes registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de donantes obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/")
    public ResponseEntity<List<ResponseDonorDTO>> GetAllDonors() {
        return ResponseEntity.ok(donorService.GetAllDonors());
    }

    @Operation(summary = "Obtener un donante por ID",
            description = "Devuelve la información detallada de un donante específico identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "Donante encontrado y devuelto correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> GetDonorById(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.GetDonorById(id));
    }

    @Operation(summary = "Crear un nuevo donante",
            description = "Registra un nuevo donante en el sistema a partir de los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Donante creado exitosamente.")
    @PostMapping("/auth")
    public ResponseEntity<ResponseDonorDTO> CreateDonor(@RequestBody RequestDonorDTO donor) {
        return ResponseEntity.ok(donorService.CreateDonor(donor));
    }

    @Operation(summary = "Actualizar un donante existente",
            description = "Actualiza la información de un donante identificado por su ID con los nuevos datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Donante actualizado correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> UpdateDonor(@RequestBody RequestDonorDTO donor, @PathVariable Long id) {
        return ResponseEntity.ok(donorService.UpdateDonor(donor, id));
    }

    @Operation(summary = "Eliminar (desactivar) un donante",
            description = "Desactiva un donante identificado por su ID, marcándolo como inactivo en el sistema.")
    @ApiResponse(responseCode = "200", description = "Donante desactivado correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> DeleteDonor(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.DeleteDonor(id));
    }

    @Operation(summary = "Métricas de donaciones del donador",
            description = "Devuelve estadísticas de donaciones: campañas asistidas, sangre estimada donada.")
    @ApiResponse(responseCode = "200", description = "Métricas obtenidas correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/{id}/metrics/stats")
    public ResponseEntity<DonorStatsDTO> GetDonorStats(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.GetDonorStats(id));
    }

    @Operation(summary = "Información de salud del donador",
            description = "Devuelve información de salud: tipo de sangre, IMC, edad, última donación, próxima fecha elegible.")
    @ApiResponse(responseCode = "200", description = "Información de salud obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR')")
    @GetMapping("/{id}/metrics/health")
    public ResponseEntity<DonorHealthDTO> GetDonorHealth(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.GetDonorHealth(id));
    }
}
