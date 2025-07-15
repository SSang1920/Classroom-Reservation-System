package com.example.classroom_reservation_system.auth.controller;

import com.example.classroom_reservation_system.auth.dto.request.FindPasswordRequest;
import com.example.classroom_reservation_system.auth.dto.request.ResetPasswordRequest;
import com.example.classroom_reservation_system.auth.service.PasswordResetService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * 비밀번호 재설정 메일 발송 요청
     */
    @PostMapping("/request-reset")
    public ResponseEntity<ApiSuccessResponse<Void>> findPassword(@RequestBody @Valid FindPasswordRequest request) {
        passwordResetService.sendResetPasswordMail(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "비밀번호 재설정 메일을 발송했습니다."));
    }

    /**
     * 비밀번호 재설정
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiSuccessResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "비밀번호가 성공적으로 변경되었습니다."));
    }
}
