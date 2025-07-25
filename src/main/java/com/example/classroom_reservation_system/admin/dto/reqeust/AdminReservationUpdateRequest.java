package com.example.classroom_reservation_system.admin.dto.reqeust;

import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminReservationUpdateRequest {

    @NotNull(message = "강의실 ID는 필수입니다.")
    private Long classroomId;

    @NotNull(message = "예약 날짜는 필수입니다.")
    @FutureOrPresent(message = "예약 날짜는 오늘 또는 미래여야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;

    @NotEmpty(message = "예약 시간은 최소 1개 이상 선택해야 합니다.")
    private List<TimePeriod> periods;

}
