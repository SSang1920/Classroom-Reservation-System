package com.example.classroom_reservation_system.request.event;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import jakarta.mail.Message;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.net.URL;

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
