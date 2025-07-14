package com.example.classroom_reservation_system.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberRegistrationViewController {

    @GetMapping("/signup")
    public String signupForm() {
        return "member/signup";
    }
}
