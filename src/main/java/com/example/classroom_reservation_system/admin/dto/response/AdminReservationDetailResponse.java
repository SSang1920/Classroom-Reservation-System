package com.example.classroom_reservation_system.admin.dto.response;

import com.example.classroom_reservation_system.member.entity.Admin;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminReservationDetailResponse {
    private Long reservationId;
    private Long classroomId;
    private String classroomName;
    private Long buildingId;
    private LocalDate reservationDate;
    private List<TimePeriod> periods;

    public static AdminReservationDetailResponse from(Reservation reservation){
        return AdminReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .classroomId(reservation.getClassroom().getId())
                .classroomName(reservation.getClassroom().getName())
                .buildingId(reservation.getClassroom().getBuilding().getId())
                .reservationDate(reservation.getStartTime().toLocalDate())
                .periods(reservation.getPeriods().stream()
                        .sorted()
                        .collect(Collectors.toList()))
                .build();
    }


}
