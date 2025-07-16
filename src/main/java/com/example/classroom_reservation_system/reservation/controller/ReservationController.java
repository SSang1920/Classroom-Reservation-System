package com.example.classroom_reservation_system.reservation.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.reservation.dto.request.ReservationRequest;
import com.example.classroom_reservation_system.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 생성 API (사용자)
     */
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<Void>> createReservation(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ReservationRequest request){
        String memberUuid = userDetails.getMemberUuid();
        Long reservationId = reservationService.createReservation(memberUuid,request);
        URI location = URI.create("/api/reservations/" + reservationId);

        return ResponseEntity.created(location)
                .body(ApiSuccessResponse.of(201, "예약이 성공적으로 생성되었습니다."));
    }

    /**
     * 예약 취소 API (사용자)
     */
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiSuccessResponse<Void>> cancelReservation(@PathVariable Long reservationId, @AuthenticationPrincipal CustomUserDetails userDetails){
        String memberUuid = userDetails.getMemberUuid();
        reservationService.cancelReservation(reservationId, memberUuid);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약이 성공적으로 취소되었습니다."));
    }

    /**
     * 예약 사용 완료 API (사용자)
     */
    @PatchMapping("/{reservationId}/complete")
    public ResponseEntity<ApiSuccessResponse<Void>> completeReservation(@PathVariable Long reservationId,@AuthenticationPrincipal CustomUserDetails userDetails){
        String memberUuid = userDetails.getMemberUuid();
        reservationService.completeReservation(reservationId, memberUuid);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약이 성공적으로 완료되었습니다."));

    }

    /**
     * 예약 강제 취소 API (관리자)
     */
    @PatchMapping("/{reservationId}/admin/cancel")
    @PreAuthorize("hasRole('ADMIN')") // 'ADMIN'을 가진 사용자만 실행 가능
    public ResponseEntity<ApiSuccessResponse<Void>> cancelReservationByAdmin(@PathVariable Long reservationId){
        reservationService.cancelReservationByAdmin(reservationId);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "관리자에 의해 예약이 취소되었습니다."));
    }

}
