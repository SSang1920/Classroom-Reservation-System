package com.example.classroom_reservation_system.member.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.member.dto.request.SignUpRequest;
import com.example.classroom_reservation_system.member.service.MemberRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRegistrationController {

    private final MemberRegistrationService memberRegistrationService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request) {
        memberRegistrationService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.of(201, "회원가입 성공"));
    }

    /**
     * ID 중복 검사 API
     */
    @GetMapping("/check-id")
    public ResponseEntity<ApiSuccessResponse<Void>> checkIdDuplicate(@RequestParam("id") String id) {
        memberRegistrationService.checkDuplicateId(id);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "사용 가능한 아이디입니다."));
    }

    /**
     * 이메일 중복 검사 API
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiSuccessResponse<Void>> checkEmailDuplicate(@RequestParam("email") String email) {
        memberRegistrationService.checkDuplicateEmail(email);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "사용 가능한 이메일입니다."));
    }
}
