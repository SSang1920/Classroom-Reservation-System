package com.example.classroom_reservation_system.controller.auth;

import com.example.classroom_reservation_system.dto.requestDto.LoginRequest;
import com.example.classroom_reservation_system.dto.requestDto.SignUpRequest;
import com.example.classroom_reservation_system.dto.requestDto.TokenRequest;
import com.example.classroom_reservation_system.dto.responseDto.ApiSuccessResponse;
import com.example.classroom_reservation_system.dto.responseDto.LoginResponse;
import com.example.classroom_reservation_system.dto.responseDto.TokenResponse;
import com.example.classroom_reservation_system.security.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.classroom_reservation_system.service.AuthService;
import com.example.classroom_reservation_system.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
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
    public ResponseEntity<ApiSuccessResponse<Void>> logout(Authentication authentication) {
        authService.logout(authentication);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "로그아웃 완료"));
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원가입 성공"));
    }

    /**
     * ID 중복 검사 API
     */
    @GetMapping("/check-id")
    public ResponseEntity<ApiSuccessResponse<Void>> checkIdDuplicate(@RequestParam("id") String id) {
        authService.checkDuplicateId(id);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "사용 가능한 아이디입니다."));
    }
}
