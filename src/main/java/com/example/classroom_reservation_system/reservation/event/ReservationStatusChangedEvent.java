package com.example.classroom_reservation_system.reservation.event;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReservationStatusChangedEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final String message;


    public ReservationStatusChangedEvent(Object source, Reservation reservation, String message){
        super(source);
        this.reservation = reservation;
        this.message = message;
    }
}
