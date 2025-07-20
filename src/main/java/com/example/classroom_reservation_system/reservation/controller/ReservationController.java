package com.example.classroom_reservation_system.reservation.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.reservation.dto.request.ReservationRequest;
import com.example.classroom_reservation_system.reservation.dto.response.ReservationCreationResponse;
import com.example.classroom_reservation_system.reservation.dto.response.ReservationResponse;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import com.example.classroom_reservation_system.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 내 예약 목록 조회 API
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // 이 엔드포인트는 인증된 사용자만 접근 가능
    public ResponseEntity<ApiSuccessResponse<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberUuid = userDetails.getMemberUuid();
        List<ReservationResponse> reservations = reservationService.getMyReservationsApi(memberUuid);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "내 예약 목록 조회 성공", reservations));
    }

    /**
     * 특정 날짜에 대한 예약 조회API
     * 사용자가 날짜 선택시 호출
     */
    @GetMapping("/classroom/{classroomId}/reserved-periods")
    public ResponseEntity<ApiSuccessResponse<Set<TimePeriod>>> getReservedPeriodsForDate(
            @PathVariable Long classroomId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
        Set<TimePeriod> reservedPeriods = reservationService.getReservedPeriodsForDate(classroomId, date);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "예약된 시간 조회 성공", reservedPeriods));
    }

    /**
     * 예약 생성 API (사용자)
     */
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<ReservationCreationResponse>> createReservation(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ReservationRequest request){
        String memberUuid = userDetails.getMemberUuid();
        Long reservationId = reservationService.createReservation(memberUuid,request);
        URI location = URI.create("/api/reservations/" + reservationId);

        //응답에 포함할 데이터 생성
        ReservationCreationResponse responseData = new ReservationCreationResponse(reservationId);

        return ResponseEntity.created(location)
                .body(ApiSuccessResponse.of(201, "예약이 성공적으로 생성되었습니다.", responseData));
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

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "사용완료 처리가 성공적으로 되었습니다."));

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
