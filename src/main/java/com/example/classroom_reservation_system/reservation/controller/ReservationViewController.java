package com.example.classroom_reservation_system.reservation.controller;

import com.example.classroom_reservation_system.config.security.CustomUserDetails;
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

    private final ReservationService reservationService;

    @GetMapping("/reservations/my-list")
    public String myReservationsPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String memberUuid = userDetails.getMemberUuid();
        List<Reservation> reservations = reservationService.getMyReservations(memberUuid);
        model.addAttribute("reservations", reservations);
        return "reservation/my-list";
    }

    @GetMapping("/reservations/new")
    public String newReservationPage(){
        return "reservation/new-form";
    }
}
