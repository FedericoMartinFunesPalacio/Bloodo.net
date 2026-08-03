package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.notification.ResponseNotificationDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;

    private Long getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UsersEntity user = usersRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @Operation(summary = "Obtener notificaciones del usuario", description = "Devuelve todas las notificaciones del usuario autenticado, ordenadas por fecha descendente.")
    @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping
    public ResponseEntity<List<ResponseNotificationDTO>> getMyNotifications() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @Operation(summary = "Obtener cantidad de no leídas", description = "Devuelve el conteo de notificaciones no leídas del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación específica como leída.")
    @ApiResponse(responseCode = "200", description = "Notificación marcada correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @PutMapping("/{id}/read")
    public ResponseEntity<ResponseNotificationDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones del usuario autenticado como leídas.")
    @ApiResponse(responseCode = "200", description = "Todas las notificaciones marcadas correctamente.")
    @PreAuthorize("hasAnyRole('ADMIN','DONOR','ORGANIZER')")
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
