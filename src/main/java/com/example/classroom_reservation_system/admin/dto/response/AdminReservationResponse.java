package com.example.classroom_reservation_system.admin.dto.response;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class AdminReservationResponse {
    private Long id;
    private String username;
    private String loginId;
    private String classroomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationState state;
    private LocalDateTime createAt;

    /**
     * DTO 변환 메서드
     * @param reservation
     * @return AdminReservationresponse
     */
    public static AdminReservationResponse from(Reservation reservation){
        return AdminReservationResponse.builder()
                .id(reservation.getId())
                .username(reservation.getMember().getName())
                .loginId(reservation.getMember().getId())
                .classroomName(reservation.getClassroom().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .state(reservation.getReservationState())
                .createAt(reservation.getCreatedAt())
                .build();
    }


}
