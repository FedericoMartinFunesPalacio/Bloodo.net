package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.notification.ResponseNotificationDTO;
import com.FedericoFunes.app_service.entities.NotificationEntity;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.NotificationRepository;
import com.FedericoFunes.app_service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private ResponseNotificationDTO entityToDTO(NotificationEntity entity) {
        ResponseNotificationDTO dto = new ResponseNotificationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Override
    public void createNotification(Long userId, String title, String message) {
        NotificationEntity entity = NotificationEntity.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(entity);
    }

    @Override
    public List<ResponseNotificationDTO> getNotificationsByUserId(Long userId) {
        List<ResponseNotificationDTO> result = new ArrayList<>();
        for (NotificationEntity entity : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            if (!entity.getIsRead()) {
                result.add(entityToDTO(entity));
            }
        }
        return result;
    }

    @Override
    public Long getUnreadCount(Long userId) {

        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public ResponseNotificationDTO markAsRead(Long notificationId) {
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        entity.setIsRead(true);
        return entityToDTO(notificationRepository.save(entity));
    }

    @Override
    public void markAllAsRead(Long userId) {
        for (NotificationEntity entity : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            if (!entity.getIsRead()) {
                entity.setIsRead(true);
                notificationRepository.save(entity);
            }
        }
    }
}
