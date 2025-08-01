package com.example.classroom_reservation_system.notice.service;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.entity.Admin;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.notice.dto.request.NoticeCreateRequest;
import com.example.classroom_reservation_system.notice.dto.request.NoticeUpdateRequest;
import com.example.classroom_reservation_system.notice.dto.response.NoticeDetailResponse;
import com.example.classroom_reservation_system.notice.dto.response.NoticeListResponse;
import com.example.classroom_reservation_system.notice.entity.Notice;
import com.example.classroom_reservation_system.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;

    /**
     * 공지사항 생성
     */
    @Transactional
    public Long createNotice(NoticeCreateRequest request, String adminUuid) {
        Admin admin = adminRepository.findByMemberUuid(adminUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(admin)
                .build();
        Notice savedNotice = noticeRepository.save(notice);

        return savedNotice.getId();
    }

    /**
     * 모든 공지사항 목록 조회
     */
    public Page<NoticeListResponse> getAllNotices(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);
        return notices.map(NoticeListResponse::from);
    }

    /**
     * 공지사항 상세 조회
     */
    @Transactional
    public NoticeDetailResponse getNoticeDetails(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 조회수 증가
        notice.incrementViewCount();

        return NoticeDetailResponse.from(notice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        noticeRepository.delete(notice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public void updateNotice(Long id, NoticeUpdateRequest request) {
        // 수정할 내용이 제목과 내용 모두 없는 경우 예외 처리
        if ((request.getTitle() == null || request.getTitle().isBlank()) &&
            (request.getContent() == null || request.getContent().isBlank())) {
            throw new CustomException(ErrorCode.NO_UPDATE_CONTENT);
        }

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        notice.update(request.getTitle(), request.getContent());
    }
}
