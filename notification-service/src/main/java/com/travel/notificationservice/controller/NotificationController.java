package com.travel.notificationservice.controller;

import com.travel.notificationservice.dto.ApiResponse;
import com.travel.notificationservice.dto.NotificationRequestDTO;
import com.travel.notificationservice.dto.NotificationResponseDTO;
import com.travel.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Service", description = "Notification APIs")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send notification")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> sendNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO notification = notificationService.sendNotification(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification sent", notification));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotificationsByUserId(
            @PathVariable Long userId) {
        List<NotificationResponseDTO> notifications =
                notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved", notifications));
    }
}