package com.example.classroom_reservation_system.reservation.dto.request;

import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {

    @NotNull(message = "강의실 ID는 필수입니다.")
    private Long classroomId;

    @NotNull(message = "예약 날짜는 필수입니다.")
    @FutureOrPresent(message = "예약 날자는 과거일수 없습니다..")
    private LocalDate reservationDate;

    @NotEmpty(message = "예약 시간은 최소 하나 이상 선택해야 합니다.")
    private List<TimePeriod> periods;
}
