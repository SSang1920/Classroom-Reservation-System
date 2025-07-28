package com.example.classroom_reservation_system.history.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryState {
    RESERVED("예약됨"),   // 예약이 성공적으로 생성되었을 때 기록
    CANCELED("취소됨"),    // 예약이 취소되었을 때 기록
    CANCELED_BY_ADMIN("관리자에 의해 취소됨"), // 관리자에 의해 취소됨
    UPDATED_BY_ADMIN("관리자에 의해 변경됨"), // 관리자에 의해 변경됨
    COMPLETED("완료됨"),  // 예약이 완료되었을 때 기록
    COMPLETED_BY_SYSTEM("자동완료됨"); // 시스템에 의해 자동 완료됨

    private final String description;
}
