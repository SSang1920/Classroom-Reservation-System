package com.example.classroom_reservation_system.reservation.dto.response;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private String classroomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;
    private final boolean hasPendingChangeRequest;

    //엔티티 -> DTO 변환 정적 팩토리 메소드
    public static  ReservationResponse from(Reservation reservation, boolean hasPendingChangeRequest){
        return ReservationResponse.builder()
                .id(reservation.getId())
                .classroomName(reservation.getClassroom().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .state(reservation.getReservationState().getDescription())
                .hasPendingChangeRequest(hasPendingChangeRequest)
                .build();
    }
}
