package com.example.classroom_reservation_system.member.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private MemberService memberService;

    /**
     * 현재 로그인한 사용자가 자신의 계정을 회원탈퇴
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteMyAccount(Authentication authentication) {
        memberService.deleteMyAccount(authentication);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원 탈퇴가 성공적으로 처리되었습니다.", null));
    }
}
