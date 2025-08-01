package com.example.classroom_reservation_system.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    /**
     * 내 정보 페이지로 이동
     */
    @GetMapping("/mypage")
    public String myPage() {
        return "member/mypage";
    }
}
