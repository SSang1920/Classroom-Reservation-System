package com.example.classroom_reservation_system.reservation.dto.response;

import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import lombok.Getter;

@Getter
public class AvailableTimeDto {
    private final String name;
    private final String time;

    private AvailableTimeDto(TimePeriod period){
        this.name = period.name();
        this.time = period.getDescription();
    }

    public static AvailableTimeDto from(TimePeriod period){
        return new AvailableTimeDto(period);
    }
}
