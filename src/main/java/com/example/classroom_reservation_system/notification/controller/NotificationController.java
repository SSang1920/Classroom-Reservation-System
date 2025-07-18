package com.example.classroom_reservation_system.notification.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.notification.dto.NotificationResponseDto;
import com.example.classroom_reservation_system.notification.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE(Server-Sent Evnets) 구독
     * 클라이언트가 실시간 알림을 받기 위해 연결하는 엔드포인트
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userDetails.getMemberUuid(), lastEventId);
    }

    /**
     * 내 알림 목록 조회
     * 사용자가 아이콘을 클릭시 호출되는 API
     */
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<NotificationResponseDto>>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponseDto> notifications = notificationService.getMyNotifications(userDetails.getMemberUuid());
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "알림 목록 조회 성공", notifications));
    }

    /**
     * 알림 읽음 처리
     * 사용자가 특정 알림을 클릭햇을 때 호출될 API
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiSuccessResponse<String>> readNotification(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.readNotification(id, userDetails.getMemberUuid());
        return ResponseEntity.ok(ApiSuccessResponse.of(200,"알림을 읽음 처리 햇습니다.", null));
    }

    /**
     * 사용자가 읽지 않은 알림 개수 조회
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiSuccessResponse<Long>> getUnreadNotification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = notificationService.getUnreadNotificationCount(userDetails.getMemberUuid());
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "읽지 않은 알림 개수 조회 성공", count));
    }
}
