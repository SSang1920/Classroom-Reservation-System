package com.example.classroom_reservation_system.member.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.member.dto.response.MyInfoResponse;
import com.example.classroom_reservation_system.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 현재 로그인한 사용자가 자신의 계정을 회원탈퇴
     */
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMyAccount(userDetails.getMemberUuid());
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원 탈퇴가 성공적으로 처리되었습니다.", null));
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccessResponse<MyInfoResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MyInfoResponse myInfo = memberService.getMyInfo(userDetails.getMemberUuid());
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "내 정보 조회 성공하였습니다.", myInfo));
    }
}
