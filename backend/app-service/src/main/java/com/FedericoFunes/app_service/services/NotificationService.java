package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.notification.ResponseNotificationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    void createNotification(Long userId, String title, String message);
    List<ResponseNotificationDTO> getNotificationsByUserId(Long userId);
    Long getUnreadCount(Long userId);
    ResponseNotificationDTO markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
