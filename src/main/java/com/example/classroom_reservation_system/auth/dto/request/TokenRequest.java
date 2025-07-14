package com.example.classroom_reservation_system.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {

    @NotBlank(message = "RefreshToken은 필수입니다.")
    private String refreshToken;
}
