package com.example.classroom_reservation_system.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString   // 로그 방지
public class LoginResponse {

    @ToString.Exclude
    private String accessToken;

    @ToString.Exclude
    private String refreshToken;
}
