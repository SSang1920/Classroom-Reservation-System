package com.example.classroom_reservation_system.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/main")
    public String adminMainPage() {
        return "admin/main";
    }

    @GetMapping("/members")
    public String adminMembersPage() {
        return "admin/members";
    }

    @GetMapping("/reservations")
    public String adminReservationsPage(){
        return "admin/reservations";
    }
}
