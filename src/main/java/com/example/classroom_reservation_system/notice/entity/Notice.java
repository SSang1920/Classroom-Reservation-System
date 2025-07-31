package com.example.classroom_reservation_system.notice.entity;

import com.example.classroom_reservation_system.member.entity.Admin;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice", indexes = {
        @Index(name = "idx_notice_created_at", columnList = "created_at"),
        @Index(name = "idx_notice_admin_id", columnList = "admin_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "author")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin author;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Notice(String title, String content, Admin author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    /**
     * 비즈니스 로직 - 업데이트
     */
    public void update(String title, String content) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    /**
     * 비즈니스 로직 - 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
}
