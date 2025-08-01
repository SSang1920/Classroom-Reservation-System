package com.example.classroom_reservation_system.request.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.request.dto.AdminFeedbackDto;
import com.example.classroom_reservation_system.request.dto.response.ReservationChangeRequestResponseDto;
import com.example.classroom_reservation_system.request.service.ChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/change-requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final ChangeRequestService changeRequestService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<ReservationChangeRequestResponseDto>>> getAllRequests() {
        List<ReservationChangeRequestResponseDto> requests = changeRequestService.getAllChangeRequests();

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "모든 예약 변경 요청 조회 성공", requests));
    }

    /**
     * 특정 변경 요청 처리 API
     */
    @PostMapping("/{requestId}/process")
    public ResponseEntity<ApiSuccessResponse<Void>> processRequest(
            @PathVariable Long requestId,
            @RequestBody AdminFeedbackDto dto) {
        changeRequestService.processChangeRequest(requestId,dto);
        String message = dto.isApprove() ? "요청이 승인되었습니다." : "요청이 거절되었습니다.";

        return ResponseEntity.ok(ApiSuccessResponse.of(200, message));
    }
}
