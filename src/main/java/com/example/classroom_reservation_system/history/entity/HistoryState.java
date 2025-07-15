package com.example.classroom_reservation_system.history.entity;

public enum HistoryState {
    RESERVED,   // 예약이 성공적으로 생성되었을 때 기록
    COMPLETED,  // 예약이 완료되었을 때 기록
    CANCELED    // 예약이 취소되었을 때 기록
}
