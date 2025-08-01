package com.example.classroom_reservation_system.notice.controller;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.notice.dto.request.NoticeCreateRequest;
import com.example.classroom_reservation_system.notice.dto.request.NoticeUpdateRequest;
import com.example.classroom_reservation_system.notice.dto.response.NoticeDetailResponse;
import com.example.classroom_reservation_system.notice.dto.response.NoticeListResponse;
import com.example.classroom_reservation_system.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 생성 API
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<Void>> createNotice(
            @Valid @RequestBody NoticeCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long noticeId = noticeService.createNotice(request, userDetails.getMemberUuid());
        URI location = URI.create("/api/notices/" + noticeId);

        return ResponseEntity.created(location).
                body(ApiSuccessResponse.of(201, "공지사항이 등록되었습니다."));
    }

    /**
     * 공지사항 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<NoticeListResponse>>> getAllNotices(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NoticeListResponse> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "공지사항 목록 조회 성공하였습니다.", notices));
    }

    /**
     * 공지사항 상세 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<NoticeDetailResponse>> getNoticeDetails(@PathVariable Long id) {
        NoticeDetailResponse notice = noticeService.getNoticeDetails(id);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "공지사항 상세 조회 성공하였습니다.", notice));
    }

    /**
     * 공지사항 수정 API
     */
    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<Void>> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeUpdateRequest request) {
        noticeService.updateNotice(id, request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "공지사항이 수정되었습니다."));
    }

    /**
     * 공지사항 삭제 API
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "공지사항이 삭제되었습니다."));
    }
}
