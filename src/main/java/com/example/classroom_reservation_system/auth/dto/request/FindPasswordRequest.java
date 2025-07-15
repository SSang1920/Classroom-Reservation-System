package com.example.classroom_reservation_system.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPasswordRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
}
