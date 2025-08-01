package com.example.classroom_reservation_system.request.event;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReservationChangeRequestedEvent extends ApplicationEvent {
    private final Reservation reservation;
    private final String message;

    public ReservationChangeRequestedEvent(Object source, Reservation reservation, String message){
        super(source);
        this.reservation = reservation;
        this.message = message;
    }
}
