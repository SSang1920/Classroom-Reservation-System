package com.example.classroom_reservation_system.reservation.controller;

import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.reservation.dto.response.ReservationResponse;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
