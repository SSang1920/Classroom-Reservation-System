package com.example.classroom_reservation_system.history.entity;

public enum HistoryState {
    RESERVED,   // 예약이 성공적으로 생성되었을 때 기록
    CANCELED,    // 예약이 취소되었을 때 기록
    CANCELED_BY_ADMIN, // 관리자에 의해 취소됨
    COMPLETED,  // 예약이 완료되었을 때 기록
    COMPLETED_BY_SYSTEM // 시스템에 의해 자동 완료됨
}
