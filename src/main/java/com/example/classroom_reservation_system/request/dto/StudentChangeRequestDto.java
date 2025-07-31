package com.example.classroom_reservation_system.request.dto;

import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class StudentChangeRequestDto {

    @NotNull(message = "변경할 강의실 ID는 필수입니다.")
    private Long newClassroomId;

    @NotNull(message = "변경할 예약 날짜는 필수입니다.")
    private LocalDate newReservationDate;

    @NotEmpty(message = "변경할 예약 시간은 최소 1개 이상 선택해야 합니다.")
    private Set<TimePeriod> newPeriods;

    private String requestMessage;
}
