package com.example.classroom_reservation_system.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {

    @NotBlank(message = "RefreshToken은 필수입니다.")
    private String refreshToken;
}
