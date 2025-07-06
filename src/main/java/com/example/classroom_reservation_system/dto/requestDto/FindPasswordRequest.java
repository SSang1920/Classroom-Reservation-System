package com.example.classroom_reservation_system.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindPasswordRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
}
