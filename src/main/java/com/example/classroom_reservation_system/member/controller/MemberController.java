package com.example.classroom_reservation_system.member.controller;

import com.example.classroom_reservation_system.auth.dto.response.TokenResponse;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.member.dto.request.MyInfoUpdateRequest;
import com.example.classroom_reservation_system.member.dto.request.PasswordChangeRequest;
import com.example.classroom_reservation_system.member.dto.response.MyInfoResponse;
import com.example.classroom_reservation_system.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 내 정보 수정
     */
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccessResponse<TokenResponse>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MyInfoUpdateRequest request) {
        TokenResponse newTokens = memberService.updateMyInfoAndReissueTokens(userDetails.getMemberUuid(), request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원 정보가 성공적으로 수정되었으며, 토큰이 갱신되었습니다.", newTokens));
    }

    /**
     * 내 비밀번호 변경
     */
    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccessResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {
        memberService.changePassword(userDetails.getMemberUuid(), request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "비밀번호가 성공적으로 변경되었습니다.", null));
    }
}
