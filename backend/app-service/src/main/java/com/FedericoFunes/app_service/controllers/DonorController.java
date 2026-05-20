package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.services.DonorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donors")
public class DonorController {
    private final DonorService donorService;

    @Operation(summary = "Obtener todos los donantes",
            description = "Devuelve una lista con todos los donantes registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de donantes obtenida correctamente.")
    @GetMapping("/")
    public ResponseEntity<List<ResponseDonorDTO>> GetAllDonors() {
        return ResponseEntity.ok(donorService.GetAllDonors());
    }

    @Operation(summary = "Obtener un donante por ID",
            description = "Devuelve la información detallada de un donante específico identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "Donante encontrado y devuelto correctamente.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> GetDonorById(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.GetDonorById(id));
    }

    @Operation(summary = "Crear un nuevo donante",
            description = "Registra un nuevo donante en el sistema a partir de los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Donante creado exitosamente.")
    @PostMapping("/")
    public ResponseEntity<ResponseDonorDTO> CreateDonor(@RequestBody RequestDonorDTO donor) {
        return ResponseEntity.ok(donorService.CreateDonor(donor));
    }

    @Operation(summary = "Actualizar un donante existente",
            description = "Actualiza la información de un donante identificado por su ID con los nuevos datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Donante actualizado correctamente.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> UpdateDonor(@RequestBody RequestDonorDTO donor, @PathVariable Long id) {
        return ResponseEntity.ok(donorService.UpdateDonor(donor, id));
    }

    @Operation(summary = "Eliminar (desactivar) un donante",
            description = "Desactiva un donante identificado por su ID, marcándolo como inactivo en el sistema.")
    @ApiResponse(responseCode = "200", description = "Donante desactivado correctamente.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDonorDTO> DeleteDonor(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.DeleteDonor(id));
    }
}
