package com.example.classroom_reservation_system.member.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyInfoUpdateRequest {

    private String name;

    @Email(message =  "유효한 이메일 형식이어야 합니다.")
    private String email;
}
