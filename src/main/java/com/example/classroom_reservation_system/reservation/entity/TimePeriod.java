package com.example.classroom_reservation_system.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public enum TimePeriod {
    PERIOD_1("1교시", java.time.LocalTime.of(9,30), java.time.LocalTime.of(10, 45)),
    PERIOD_2("2교시", java.time.LocalTime.of(11,0), java.time.LocalTime.of(12, 15)),
    PERIOD_3("3교시", java.time.LocalTime.of(13,0), java.time.LocalTime.of(14, 15)),
    PERIOD_4("4교시", java.time.LocalTime.of(14,30), java.time.LocalTime.of(15, 45)),
    PERIOD_5("5교시", java.time.LocalTime.of(16,0), java.time.LocalTime.of(17, 15)),
    PERIOD_6("6교시", java.time.LocalTime.of(17,30), java.time.LocalTime.of(18, 45)),
    PERIOD_7("7교시", java.time.LocalTime.of(19,0), java.time.LocalTime.of(20, 15)),
    PERIOD_8("8교시", java.time.LocalTime.of(20,30), java.time.LocalTime.of(21, 45));


    private final String description;
    private final LocalTime startTime;
    private final LocalTime endTime;

}
