package com.example.classroom_reservation_system.dto.requestDto;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String id;      // 학번, 교번, 관리자번호
    private String password;
}
