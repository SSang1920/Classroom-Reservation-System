package com.example.classroom_reservation_system.admin.controller;

import com.example.classroom_reservation_system.admin.dto.request.AdminReservationUpdateRequest;
import com.example.classroom_reservation_system.admin.dto.response.AdminReservationDetailResponse;
import com.example.classroom_reservation_system.admin.dto.response.AdminReservationResponse;
import com.example.classroom_reservation_system.admin.service.AdminReservationService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    /**
     * 관리자용 예약 목록 검색 API
     */
    @GetMapping("/reservations")
    public ResponseEntity<ApiSuccessResponse<Page<AdminReservationResponse>>> getReservations(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long classroomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false)ReservationState state,
            Pageable pageable
        ){
        Page<AdminReservationResponse> reservations = adminReservationService.getReservations(
                username, classroomId, startDate, endDate, state, pageable
        );
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약 목록 조회 성공", reservations));
    }

    /**
     * 관리자용 예약 강제취소 API
     */
    @PatchMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<ApiSuccessResponse<Void>> cancelReservationByAdmin(@PathVariable Long reservationId){
        adminReservationService.cancelReservationByAdmin(reservationId);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "관리자에 의해 예약이 취소되었습니다."));
    }

    /**
     * 관리자용 단일 예약 상세 조회 API
     * @param reservationId
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiSuccessResponse<AdminReservationDetailResponse>> getReservationDetails(@PathVariable Long reservationId){
        AdminReservationDetailResponse reservationDetails = adminReservationService.getReservationDetails(reservationId);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약 상세 정보 조회 성공", reservationDetails));
    }

    /**
     * 관리자용 예약 변경 API
     */
    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiSuccessResponse<Void>> updateReservationByAdmin(
            @PathVariable Long reservationId,
            @Valid @RequestBody AdminReservationUpdateRequest request
    ){
        adminReservationService.updateReservationByAdmin(reservationId, request);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "관리자에 의해 예약이 변경되었습니다."));
    }





}
