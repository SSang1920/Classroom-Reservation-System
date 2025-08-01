package com.example.classroom_reservation_system.request.dto.response;


import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationChangeRequestResponseDto {
    private Long requestId;
    private String studentName;
    private String originalClassroom;
    private LocalDateTime originalStartTime;
    private Long newClassroomId;
    private LocalDate newReservationDate;
    private String requestMessage;
    private String status;
    private LocalDateTime requestedAt;

    public static ReservationChangeRequestResponseDto from(ReservationChangeRequest entity) {
        return ReservationChangeRequestResponseDto.builder()
                .requestId(entity.getId())
                .studentName(entity.getReservation().getMember().getName())
                .originalClassroom(entity.getOriginalClassroomName())
                .originalStartTime(entity.getOriginalStartTime())
                .newClassroomId(entity.getNewClassroomId())
                .newReservationDate(entity.getNewReservationDate())
                .requestMessage(entity.getRequestMessage())
                .status(entity.getStatus().getDescription())
                .requestedAt(entity.getCreatedAt())
                .build();
    }
}
