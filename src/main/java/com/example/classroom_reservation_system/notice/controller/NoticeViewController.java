package com.example.classroom_reservation_system.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NoticeViewController {

    @GetMapping("/notices")
    private String allNotice() {
        return "notices";
    }
}
