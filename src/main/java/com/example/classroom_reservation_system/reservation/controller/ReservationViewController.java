package com.example.classroom_reservation_system.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReservationViewController {

    @GetMapping("/history")
    public String myReservationHistoryPage(){

        return "reservation/history";
    }

    @GetMapping("/reserve")
    public String newReservationPage(){

        return "reservation/reserve";
    }
}
