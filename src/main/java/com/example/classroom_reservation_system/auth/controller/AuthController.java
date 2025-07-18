package com.example.classroom_reservation_system.auth.controller;

import com.example.classroom_reservation_system.auth.dto.request.LoginRequest;
import com.example.classroom_reservation_system.auth.dto.request.TokenRequest;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.auth.dto.response.LoginResponse;
import com.example.classroom_reservation_system.auth.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.classroom_reservation_system.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "로그인 성공", response));
    }

    /**
     * AccessToken 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiSuccessResponse<TokenResponse>> refresh(@RequestBody @Valid TokenRequest request) {
        TokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "토큰 재발급 성공", response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authService.logout(authentication);
        return ResponseEntity.noContent().build();
    }
}
