package com.example.classroom_reservation_system.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationState {
    RESERVED("예약됨"),
    COMPLETED("사용 완료"),
    CANCELED("취소됨"),
    CANCELED_BY_ADMIN("관리자에 의해 취소됨"),
    MODIFIED_BY_ADMIN("관리자에 의해 변경됨"),
    COMPLETED_BY_SYSTEM("자동 완료됨");

    private final String description;


}
