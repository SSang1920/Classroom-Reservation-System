package com.example.classroom_reservation_system.request.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.request.dto.AdminFeedbackDto;
import com.example.classroom_reservation_system.request.dto.StudentChangeRequestDto;
import com.example.classroom_reservation_system.request.service.ChangeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final MemberRepository memberRepository;

    @PostMapping("/reservations/{reservationId}/change-requests")
    public ResponseEntity<ApiSuccessResponse<Void>> createChangeRequest(
            @PathVariable Long reservationId,
            @Valid @RequestBody StudentChangeRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String memberUuid = userDetails.getMemberUuid();

        Member student = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        changeRequestService.createChangeRequest(reservationId,dto,student);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약 변경 요청이 성공적으로 접수되었습니다."));
    }

    @PatchMapping("/admin/change-requests/{requestId}")
    public ResponseEntity<ApiSuccessResponse<Void>> processChangeRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody AdminFeedbackDto dto){
        changeRequestService.processChangeRequest(requestId,dto);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "요청이 성공적으로 처리되었습니다."));
    }



}
