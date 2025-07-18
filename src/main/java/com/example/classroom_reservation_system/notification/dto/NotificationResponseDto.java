package com.example.classroom_reservation_system.notification.dto;


import com.example.classroom_reservation_system.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    //Notification 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static NotificationResponseDto from(Notification notification){
        return new NotificationResponseDto(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
            );
    }
}
