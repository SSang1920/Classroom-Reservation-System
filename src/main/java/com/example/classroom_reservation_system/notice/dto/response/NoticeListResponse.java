package com.example.classroom_reservation_system.notice.dto.response;

import com.example.classroom_reservation_system.notice.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeListResponse {

    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;
    private int viewCount;

    // DTO로 변환
    public static NoticeListResponse from(Notice notice) {
        return NoticeListResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .authorName(notice.getAuthor().getName())
                .createdAt(notice.getCreatedAt())
                .viewCount(notice.getViewCount())
                .build();
    }
}
