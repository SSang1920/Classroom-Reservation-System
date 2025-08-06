package com.example.classroom_reservation_system.notification.dto;


import com.example.classroom_reservation_system.notification.entity.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {

    private Long id;
    private String message;

    @JsonProperty("isRead") //
    private boolean isRead;

    private LocalDateTime createdAt;
    private String destinationPath;

    //Notification 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static NotificationResponseDto from(Notification notification){
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .destinationPath(notification.getType().getDestinationPath())
                .build();

    }
}
