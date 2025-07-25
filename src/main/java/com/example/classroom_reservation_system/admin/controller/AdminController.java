package com.example.classroom_reservation_system.admin.controller;

import com.example.classroom_reservation_system.admin.dto.response.AdminMemberListResponse;
import com.example.classroom_reservation_system.admin.service.AdminService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * 회원 목록을 검색 조건에 따라 조회
     *
     * @param pageable 페이징 정보
     */
    @GetMapping("/members")
    public ResponseEntity<ApiSuccessResponse<Page<AdminMemberListResponse>>> getMemberList(
            // 검색 조건 파라미터
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
        Page<AdminMemberListResponse> memberList = adminService.searchMembers(loginId, name, email, role, date, pageable);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원 목록 조회에 성공했습니다.", memberList));
    }

    /**
     * 관리자가 특정 회원을 삭제
     * @param memberPkId 삭제할 회원의 PK
     */
    @DeleteMapping("/members/{memberPkId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteMember(@PathVariable Long memberPkId) {
        adminService.deleteMember(memberPkId);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "회원이 성공적으로 삭제되었습니다.", null));
    }
}
