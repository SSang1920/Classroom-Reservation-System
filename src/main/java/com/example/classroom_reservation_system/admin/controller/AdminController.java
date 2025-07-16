package com.example.classroom_reservation_system.admin.controller;

import com.example.classroom_reservation_system.admin.dto.response.AdminMemberListResponse;
import com.example.classroom_reservation_system.admin.service.AdminService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/members")
    public ResponseEntity<ApiSuccessResponse<Page<AdminMemberListResponse>>> getMemberList(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<AdminMemberListResponse> memberList = adminService.getMemberList(pageable);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "전체 회원 조회 성공", memberList));
    }


}
