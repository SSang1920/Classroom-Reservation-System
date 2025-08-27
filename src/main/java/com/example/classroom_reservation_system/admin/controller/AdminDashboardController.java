package com.example.classroom_reservation_system.admin.controller;

import com.example.classroom_reservation_system.admin.dto.response.DashboardStatisticsResponse;
import com.example.classroom_reservation_system.admin.service.AdminDashboardService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<ApiSuccessResponse<DashboardStatisticsResponse>> getStatistics() {
        DashboardStatisticsResponse statistics = adminDashboardService.getDashboardStatistics();
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "대시보드 통계 조회 성공", statistics));
    }
}
