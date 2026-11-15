package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.organizeremp.RequestOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.services.OrganizerEmpService;
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
@RequestMapping("/api/v1/organizer-emps")
public class OrganizerEmpController {
    private final OrganizerEmpService organizerEmpService;

    @Operation(summary = "Obtener todos los organizadores empresariales",
            description = "Devuelve una lista con todos los organizadores empresariales registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de organizadores empresariales obtenida correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/")
    public ResponseEntity<List<ResponseOrganizerEmpDTO>> GetAllOrganizerEmps() {
        return ResponseEntity.ok(organizerEmpService.GetAllOrganizerEmps());
    }

    @Operation(summary = "Obtener un organizador empresarial por ID",
            description = "Devuelve la información detallada de un organizador empresarial identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "Organizador empresarial encontrado y devuelto correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseOrganizerEmpDTO> GetOrganizerEmpById(@PathVariable Long id) {
        return ResponseEntity.ok(organizerEmpService.GetOrganizerEmpById(id));
    }

    @Operation(summary = "Crear un nuevo organizador empresarial",
            description = "Registra un nuevo organizador empresarial en el sistema a partir de los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Organizador empresarial creado exitosamente.")
    @PostMapping("/auth")
    public ResponseEntity<ResponseOrganizerEmpDTO> CreateOrganizerEmp(@RequestBody RequestOrganizerEmpDTO organizerEmp) {
        return ResponseEntity.ok(organizerEmpService.CreateOrganizerEmp(organizerEmp));
    }

    @Operation(summary = "Actualizar un organizador empresarial existente",
            description = "Actualiza la información de un organizador empresarial identificado por su ID con los nuevos datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Organizador empresarial actualizado correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseOrganizerEmpDTO> UpdateOrganizerEmp(@RequestBody RequestOrganizerEmpDTO organizerEmp, @PathVariable Long id) {
        return ResponseEntity.ok(organizerEmpService.UpdateOrganizerEmp(organizerEmp, id));
    }

    @Operation(summary = "Eliminar (desactivar) un organizador empresarial",
            description = "Desactiva un organizador empresarial identificado por su ID, marcándolo como inactivo en el sistema.")
    @ApiResponse(responseCode = "200", description = "Organizador empresarial desactivado correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseOrganizerEmpDTO> DeleteOrganizerEmp(@PathVariable Long id) {
        return ResponseEntity.ok(organizerEmpService.DeleteOrganizerEmp(id));
    }
}

