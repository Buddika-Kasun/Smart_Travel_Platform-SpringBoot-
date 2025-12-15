package com.travel.notificationservice.service;

import com.travel.notificationservice.dto.NotificationRequestDTO;
import com.travel.notificationservice.dto.NotificationResponseDTO;
import com.travel.notificationservice.entity.Notification;
import com.travel.notificationservice.entity.NotificationStatus;
import com.travel.notificationservice.entity.NotificationType;
import com.travel.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        log.info("Sending notification to user: {}", request.getUserId());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);

        Notification saved = notificationRepository.save(notification);

        // Simulate sending notification
        try {
            log.info("Simulating email/SMS sending...");
            Thread.sleep(1000);

            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());
            notificationRepository.save(saved);

            log.info("Notification sent successfully to user: {}", request.getUserId());
        } catch (InterruptedException e) {
            log.error("Failed to send notification", e);
            saved.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(saved);
        }

        return mapToDTO(saved);
    }

    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private NotificationResponseDTO mapToDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getUserId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getSentAt()
        );
    }
}