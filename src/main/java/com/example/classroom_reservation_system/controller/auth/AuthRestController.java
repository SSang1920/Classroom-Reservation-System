package com.example.classroom_reservation_system.controller.auth;

import com.example.classroom_reservation_system.dto.requestDto.*;
import com.example.classroom_reservation_system.dto.responseDto.ApiSuccessResponse;
import com.example.classroom_reservation_system.dto.responseDto.LoginResponse;
import com.example.classroom_reservation_system.dto.responseDto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.classroom_reservation_system.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

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

    /**
     * 이메일 중복 검사 API
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiSuccessResponse<Void>> checkEmailDuplicate(@RequestParam("email") String email) {
        authService.checkDuplicateEmail(email);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "사용 가능한 이메일입니다."));
    }

    /**
     * 비밀번호 재설정 메일 발송
     */
    @PostMapping("/find-password")
    public ResponseEntity<ApiSuccessResponse<Void>> findPassword(@RequestBody @Valid FindPasswordRequest request) {
        authService.sendResetPasswordMail(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "비밀번호 재설정 메일을 발송했습니다."));
    }

    /**
     * 비밀번호 재설정
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiSuccessResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "비밀번호가 성공적으로 변경되었습니다."));
    }
}
