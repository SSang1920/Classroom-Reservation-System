package com.example.classroom_reservation_system.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {

    @NotBlank(message = "토큰이 필요합니다.")
    private String token;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String confirmPassword;

}
