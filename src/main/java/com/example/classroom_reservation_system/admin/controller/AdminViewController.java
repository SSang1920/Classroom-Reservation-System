package com.example.classroom_reservation_system.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @ModelAttribute("currentPath")
    public String addCurrentPathToModel(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/main")
    public String adminMainPage() {
        return "admin/main";
    }

    @GetMapping("/members")
    public String adminMembersPage() {
        return "admin/members";
    }

    @GetMapping("/reservations")
    public String adminReservationsPage() {
        return "admin/reservations";
    }

    @GetMapping("/requests")
    public String adminRequestsPage() {
        return "admin/requests";
    }

}
