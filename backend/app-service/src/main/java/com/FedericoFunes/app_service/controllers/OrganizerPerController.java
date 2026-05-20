package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.organizerper.RequestOrganizerPerDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.services.OrganizerPerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organizer-pers")
public class OrganizerPerController {
    private final OrganizerPerService organizerPerService;

    @Operation(summary = "Obtener todos los organizadores personales",
            description = "Devuelve una lista con todos los organizadores personales activos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de organizadores personales obtenida correctamente.")
    @GetMapping("/")
    public ResponseEntity<List<ResponseOrganizerPerDTO>> GetAllOrganizerPers() {
        return ResponseEntity.ok(organizerPerService.GetAllOrganizerPers());
    }

    @Operation(summary = "Obtener un organizador personal por ID",
            description = "Devuelve la información detallada de un organizador personal identificado por su ID, solo si está activo.")
    @ApiResponse(responseCode = "200", description = "Organizador personal encontrado y devuelto correctamente.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseOrganizerPerDTO> GetOrganizerPerById(@PathVariable Long id) {
        return ResponseEntity.ok(organizerPerService.GetOrganizerPerById(id));
    }

    @Operation(summary = "Crear un nuevo organizador personal",
            description = "Registra un nuevo organizador personal en el sistema a partir de los datos proporcionados. La latitud y longitud se obtienen automáticamente.")
    @ApiResponse(responseCode = "200", description = "Organizador personal creado exitosamente.")
    @PostMapping("/")
    public ResponseEntity<ResponseOrganizerPerDTO> CreateOrganizerPer(@RequestBody RequestOrganizerPerDTO organizerPer) {
        return ResponseEntity.ok(organizerPerService.CreateOrganizerPer(organizerPer));
    }

    @Operation(summary = "Actualizar un organizador personal existente",
            description = "Actualiza la información de un organizador personal identificado por su ID con los nuevos datos proporcionados. Solo si está activo.")
    @ApiResponse(responseCode = "200", description = "Organizador personal actualizado correctamente.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseOrganizerPerDTO> UpdateOrganizerPer(@RequestBody RequestOrganizerPerDTO organizerPer, @PathVariable Long id) {
        return ResponseEntity.ok(organizerPerService.UpdateOrganizerPer(organizerPer, id));
    }

    @Operation(summary = "Eliminar (desactivar) un organizador personal",
            description = "Desactiva un organizador personal identificado por su ID, marcándolo como inactivo en el sistema.")
    @ApiResponse(responseCode = "200", description = "Organizador personal desactivado correctamente.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseOrganizerPerDTO> DeleteOrganizerPer(@PathVariable Long id) {
        return ResponseEntity.ok(organizerPerService.DeleteOrganizerPer(id));
    }
}

