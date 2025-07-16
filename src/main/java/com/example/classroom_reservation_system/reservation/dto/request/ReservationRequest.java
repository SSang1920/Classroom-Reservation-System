package com.example.classroom_reservation_system.reservation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequest {

    @NotNull(message = "강의실 ID는 필수입니다.")
    private Long classroomId;

    @NotNull(message = "예약 시작 시간은 필수입니다.")
    @Future(message = "예약 시작시간이 이전 예약의 종료시간보다 빠릅니다.")
    private LocalDateTime startTime;

    @NotNull(message = "예약 종료 시간은 필수입니다.")
    @Future(message = "예약 종료시간이 다음 예약의 시작시간보다 늦습니다.")
    private LocalDateTime endTime;
}
